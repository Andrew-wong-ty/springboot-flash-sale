package com.project.concurrency.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Rabbit MQ 发送消息
 */
@Service
@Slf4j
public class MQSender {
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送秒杀信息
     * @param message
     */
    public void sendSecKillMessage(String message){
        log.info("发送消息"+message);
        rabbitTemplate.convertAndSend("seckillEXCHANGE","seckill.message",message);
    }

//    public void send(Object msg){
//        log.info("Sent message:"+msg);
////        rabbitTemplate.convertAndSend("queue",msg); // 把消息发到配置好的queue队列
//        rabbitTemplate.convertAndSend("fanoutEXCHANGE","",msg); // 把消息发到交换机, 然后再广播
//    }
//
//    public void sendDirect01(Object msg){
//        log.info("发送红色消息:"+msg);
//        rabbitTemplate.convertAndSend("directEXCHANGE","queue.red",msg); // 把消息发到和路由key对应的queue
//    }
//
//    public void sendDirect02(Object msg){
//        log.info("发送绿色消息:"+msg);
//        rabbitTemplate.convertAndSend("directEXCHANGE","queue.green",msg); // 把消息发到和路由key对应的queue
//    }
}
