package com.project.concurrency.controller;
import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.project.concurrency.pojo.Order;
import com.project.concurrency.pojo.SeckillOrder;
import com.project.concurrency.pojo.User;
import com.project.concurrency.service.IGoodsService;
import com.project.concurrency.service.IOrderService;
import com.project.concurrency.service.ISeckillOrderService;
import com.project.concurrency.utils.vo.GoodsVo;
import com.project.concurrency.utils.vo.RespBean;
import com.project.concurrency.utils.vo.RespBeanEnum;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@Controller
@RequestMapping("/secondSkill")
public class SecondKillController {

    @Resource
    private IGoodsService goodsService;
    @Resource
    private ISeckillOrderService seckillOrderService;
    @Resource
    private IOrderService orderService;
    @Resource
    private RedisTemplate redisTemplate;
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
//        model.addAttribute("user", user);
        //获取用户要抢购的商品
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);

        //判断库存
        if(goodsVo.getStockCount()<1){
//            model.addAttribute("errmsg", RespBeanEnum.STOCK_ERROR.getMessage());
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        //判断重复抢购
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


}
