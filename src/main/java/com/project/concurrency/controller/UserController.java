package com.project.concurrency.controller;


import com.project.concurrency.rabbitmq.MQSender;
import com.project.concurrency.utils.vo.RespBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zhoubin
 * @since 2022-11-13
 */
@Controller
@RequestMapping("/user")
public class UserController {
    @Resource
    private MQSender mqSend;


    /**
     * 测试使用RabbitMQ发消息
     */
//    @RequestMapping("/mq")
//    @ResponseBody
//    public void mq_test(){
//        mqSend.send("hello, mq");
//    }
//
//    @RequestMapping("/mq/fanout")
//    @ResponseBody
//    public void mq_fanout(){
//        mqSend.send("hello, fanout");
//    }
//
//    @RequestMapping("/mq/direct01")
//    @ResponseBody
//    public void mq_direct01(){
//        mqSend.sendDirect01("hello, RED");
//    }
//
//    @RequestMapping("/mq/direct02")
//    @ResponseBody
//    public void mq_direct02(){
//        mqSend.sendDirect02("hello, GREEN");
//    }

}
