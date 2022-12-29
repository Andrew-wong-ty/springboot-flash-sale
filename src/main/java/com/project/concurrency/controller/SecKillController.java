package com.project.concurrency.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.project.concurrency.pojo.Order;
import com.project.concurrency.pojo.SeckillOrder;
import com.project.concurrency.pojo.User;
import com.project.concurrency.service.IGoodsService;
import com.project.concurrency.service.IOrderService;
import com.project.concurrency.service.ISeckillOrderService;
import com.project.concurrency.utils.vo.GoodsVo;
import com.project.concurrency.utils.vo.RespBeanEnum;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

//@Controller
//@RequestMapping("/secKill")
//public class SecKillController {
//
//    @Resource
//    private IGoodsService goodsService;
//    @Resource
//    private ISeckillOrderService seckillOrderService;
//    @Resource
//    private IOrderService orderService;
//
//    @RequestMapping("/doSecKill")
//    public String doSecKill(Model model,long goodsId ,HttpServletRequest request){
//        return "hello";
//    }
//
//}

































//    @RequestMapping("/doSecKill")
//    public String doSecKill(Model model, User user, long goodsId, HttpServletRequest request){
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
//        //进行抢购, 下订单, 使用order service
//        Order order = orderService.secKill(user, goodsVo);
//        model.addAttribute("orders",order);
//        model.addAttribute("goods",goodsVo);
//        int debug_stop = 1;
//        return "orderDetail";
//    }