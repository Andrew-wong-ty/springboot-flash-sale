package com.project.concurrency.controller;


import com.project.concurrency.pojo.User;
import com.project.concurrency.service.IOrderService;
import com.project.concurrency.utils.vo.OrderDetailVo;
import com.project.concurrency.utils.vo.RespBean;
import com.project.concurrency.utils.vo.RespBeanEnum;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zhoubin
 * @since 2022-11-15
 */
@Controller
@RequestMapping("/order")
public class OrderController {


    @Resource
    private IOrderService orderService;
    /**
     * 根据orderId来获取订单详情
     * @param user
     * @param orderId
     * @return
     */
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @ResponseBody
    public RespBean detail(User user, Long orderId){
        if(user==null) return RespBean.error(RespBeanEnum.USER_NOT_FOUND_ERROR);
        OrderDetailVo orderDetailVo = orderService.detail(orderId);
        return RespBean.success(orderDetailVo);
    }

}
