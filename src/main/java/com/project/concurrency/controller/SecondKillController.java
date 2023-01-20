package com.project.concurrency.controller;
import java.util.Collections;
import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.project.concurrency.pojo.Order;
import com.project.concurrency.pojo.SecKillMessage;
import com.project.concurrency.pojo.SeckillOrder;
import com.project.concurrency.pojo.User;
import com.project.concurrency.rabbitmq.MQSender;
import com.project.concurrency.service.IGoodsService;
import com.project.concurrency.service.IOrderService;
import com.project.concurrency.service.ISeckillOrderService;
import com.project.concurrency.utils.JsonUtil;
import com.project.concurrency.utils.vo.GoodsVo;
import com.project.concurrency.utils.vo.RespBean;
import com.project.concurrency.utils.vo.RespBeanEnum;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/secondSkill")
public class SecondKillController implements InitializingBean {

    @Resource
    private IGoodsService goodsService;
    @Resource
    private ISeckillOrderService seckillOrderService;
    @Resource
    private IOrderService orderService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private MQSender mqSender;
    @Resource
    private RedisScript<Long> script;

    private HashMap<Long, Boolean> emptyStockMap = new HashMap<>(); // 在内存中标记, 判断某件商品有没有卖完
    /**
     * 以下是没有静态化之前的版本
     */
//    @RequestMapping("/doSecondKill")
//    public String doSecKill(Model model, User user, long goodsId) {
//        if(user==null) return "login";
//        model.addAttribute("user", user);
//        //获取用户要抢购的商品
//        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
//
//        //判断库存
//        if(goodsVo.getStockCount()<1){
//            model.addAttribute("errmsg", RespBeanEnum.STOCK_ERROR.getMessage());
//            return "secKillFail";
//        }
//
//        //判断重复抢购
//        SeckillOrder seckillOrder = seckillOrderService.getOne(
//                new QueryWrapper<SeckillOrder>()
//                        .eq("user_id", user.getId()).eq("goods_id",goodsId)
//                /*查询此用户有没有购买过这件商品*/
//        );
//        if(seckillOrder!=null){
//            //已经抢购过
//            model.addAttribute("errmsg", RespBeanEnum.REPEATED_ORDER_ERROR.getMessage());
//            return "secKillFail";
//        }
//
//
//
//        //进行抢购, 下订单, 使用order service
//        Order order = orderService.secKill(user, goodsVo);
//
////        Long id = order.getId();
////        Long userId = order.getUserId();
////        Long goodsId = order.getGoodsId();
////        Long deliveryAddrId = order.getDeliveryAddrId();
////        String goodsName = order.getGoodsName();
////        Integer goodsCount = order.getGoodsCount();
////        BigDecimal goodsPrice = order.getGoodsPrice();
////        Integer orderChannel = order.getOrderChannel();
////        Integer status = order.getStatus();
////        Date createDate = order.getCreateDate();
////        Date payDate = order.getPayDate();
//
//        model.addAttribute("order",order);
//        model.addAttribute("goods",goodsVo);
//        int debug_stop = 1;
//        return "orderDetailTest";
//    }


    @RequestMapping(value="/doSecondKill", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSecKill(Model model, User user, long goodsId) {
        if(user==null) {
            return RespBean.error(RespBeanEnum.USER_NOT_FOUND_ERROR);
        }

        // 在内存标记中查询是否卖完
        if(emptyStockMap.get(goodsId)) return RespBean.error(RespBeanEnum.EMPTY_STOCK);

        // redis操作
        ValueOperations valueOperations = redisTemplate.opsForValue();

        // 判断是否重复抢购
        SeckillOrder seckillOrder =
                (SeckillOrder) redisTemplate.opsForValue().get("order:"+user.getId()+":"+goodsId);
        if(seckillOrder!=null){
            return RespBean.error(RespBeanEnum.REPEAT_PURCHASE); //已经抢购过
        }

        // 减库存 (decrement是一个原子操作 )
        /**
         * 之前的这个decrement操作, 可能会这样子: 同一个用户多线程购买,
         * 若其中2个线程可能同时执行decrement操作, 从而导致数据库和redis的数据不一致.
         * 因此这里要使用lua脚本来绑定多个redis操作变成原子操作
         */
//        Long leftStockCount = valueOperations.decrement("seckillGoods:" + goodsId);
        // 使用redis script来减库存
        Long leftStockCount = (Long) redisTemplate.execute(
                script,
                Collections.singletonList("seckillGoods:" + goodsId),
                Collections.EMPTY_LIST);
        if(leftStockCount<0){
            emptyStockMap.put(goodsId,true); //标记为卖完了
//            valueOperations.increment("seckillGoods:" + goodsId); // 加回来, 变成0
            return  RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        // 进行下单
        SecKillMessage secKillMessage = new SecKillMessage(user, goodsId);
        mqSender.sendSecKillMessage(JsonUtil.object2JsonStr(secKillMessage)); // 注意, MQ发送的消息一定要序列化
        return RespBean.success(0); //0用来标记正在排队中, 让前端处理去

    }

    @RequestMapping(value="/doSecondKill(使用Redis分布式锁之前)", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSecKill_V3(Model model, User user, long goodsId) {
        if(user==null) {
            return RespBean.error(RespBeanEnum.USER_NOT_FOUND_ERROR);
        }

        // 在内存标记中查询是否卖完
        if(emptyStockMap.get(goodsId)) return RespBean.error(RespBeanEnum.EMPTY_STOCK);

        // redis操作
        ValueOperations valueOperations = redisTemplate.opsForValue();

        // 判断是否重复抢购
        SeckillOrder seckillOrder =
                (SeckillOrder) redisTemplate.opsForValue().get("order:"+user.getId()+":"+goodsId);
        if(seckillOrder!=null){
            return RespBean.error(RespBeanEnum.REPEAT_PURCHASE); //已经抢购过
        }

        // 减库存 (decrement是一个原子操作 )
        Long leftStockCount = valueOperations.decrement("seckillGoods:" + goodsId);
        if(leftStockCount<0){
            emptyStockMap.put(goodsId,true); //标记为卖完了
            valueOperations.increment("seckillGoods:" + goodsId); // 加回来, 变成0
            return  RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        // 进行下单
        SecKillMessage secKillMessage = new SecKillMessage(user, goodsId);
        mqSender.sendSecKillMessage(JsonUtil.object2JsonStr(secKillMessage)); // 注意, MQ发送的消息一定要序列化
        return RespBean.success(0); //0用来标记正在排队中, 让前端处理去

    }

    /**
     * 用于给前端查询是否抢购成功
     * (改进: 之前有这样一个操作, 秒杀成功后, 订单会放进redis, 因此直接在redis中查询是否秒杀成功就可以了)
     * @return orderId成功, -1:秒杀失败, 0:正在排队
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public RespBean getResult(User user, Long goodsId){
        if(user==null) return RespBean.error(RespBeanEnum.USER_NOT_FOUND_ERROR);
        Long orderId = seckillOrderService.getResult(user, goodsId);
        return RespBean.success(orderId);
    }

    @RequestMapping(value="/doSecondKill(redis重复抢购优化后, redis优化判断库存数量前)", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSecKill_V2(Model model, User user, long goodsId) {
        if(user==null) {
            return RespBean.error(RespBeanEnum.USER_NOT_FOUND_ERROR);
        }
//        model.addAttribute("user", user);
        //获取用户要抢购的商品
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);

        //判断库存
        if(goodsVo.getStockCount()<1){
//            model.addAttribute("errmsg", RespBeanEnum.STOCK_ERROR.getMessage());
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

//        判断重复抢购
//        SeckillOrder seckillOrder = seckillOrderService.getOne(
//                new QueryWrapper<SeckillOrder>()
//                        .eq("user_id", user.getId()).eq("goods_id",goodsId)
//                /*查询此用户有没有购买过这件商品*/
//        );
//        if(seckillOrder!=null){
//            //已经抢购过
//            model.addAttribute("errmsg", RespBeanEnum.REPEATED_ORDER_ERROR.getMessage());
//            return RespBean.error(RespBeanEnum.REPEAT_PURCHASE);
//        }
        // 优化, 使用redis判断是否重复抢购
        SeckillOrder seckillOrder =
                (SeckillOrder) redisTemplate.opsForValue().get("order:"+user.getId()+":"+goodsVo.getId());
        if(seckillOrder!=null){
            //已经抢购过
//            model.addAttribute("errmsg", RespBeanEnum.REPEATED_ORDER_ERROR.getMessage());
            return RespBean.error(RespBeanEnum.REPEAT_PURCHASE);
        }


        //进行抢购, 下订单, 使用order service
        Order order = orderService.secKill(user, goodsVo);
//        model.addAttribute("order",order);
//        model.addAttribute("goods",goodsVo);
        return RespBean.success(order);
    }

    /**
     * backend系统初始化的时候, 就把商品库存加入redis
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.findGoodsVo();
        if(CollectionUtils.isEmpty(list)) return;
        list.forEach(goodsVo -> {
            // 把键值对 "商品id:数量" 存到redis中
            redisTemplate.opsForValue().set("seckillGoods:"+goodsVo.getId(),goodsVo.getStockCount());
            // 初始化emptyStockMap, 标记为False, 没买完
            emptyStockMap.put(goodsVo.getId(),false);
        });


    }
}
