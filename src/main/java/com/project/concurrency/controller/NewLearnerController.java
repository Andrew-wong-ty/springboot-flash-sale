package com.project.concurrency.controller;


import com.project.concurrency.service.IGoodsService;
import com.project.concurrency.service.IOrderService;
import com.project.concurrency.service.ISeckillOrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

//@Controller
//@RequestMapping("/learn")
//public class NewLearnerController {
//    @RequestMapping("/request")
//    public String test1(Model model, HttpServletRequest request){
//        return "hello";
//    }
//}


@Controller
@RequestMapping("/learn")
public class NewLearnerController {

    @Resource
    private IGoodsService goodsService;
    @Resource
    private ISeckillOrderService seckillOrderService;
    @Resource
    private IOrderService orderService;

    @RequestMapping("/request")
    public String doSecKill(Model model,long goodsId , HttpServletRequest request) {
        return "hello";
    }

}