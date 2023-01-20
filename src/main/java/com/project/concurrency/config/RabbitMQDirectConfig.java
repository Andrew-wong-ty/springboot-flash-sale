package com.project.concurrency.config;


import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQDirectConfig {
//    private static final String QUEUE01 = "queue_direct01";
//    private static final String QUEUE02 = "queue_direct02";
//    private static final String EXCHANGE = "directEXCHANGE";
//
//    private static final String ROUTINGKEY01 = "queue.red";
//    private static final String ROUTINGKEY02 = "queue.green";
//
//    @Bean
//    public Queue directQueue01(){
//        return new Queue(QUEUE01);
//    }
//
//    @Bean
//    public Queue directQueue02(){
//        return new Queue(QUEUE02);
//    }
//
//    @Bean
//    public DirectExchange directExchange(){
//        return new DirectExchange(EXCHANGE); //交换机
//    }
//
//    @Bean
//    public Binding directBinding01(){
//        return BindingBuilder.bind(directQueue01()).to(directExchange()).with(ROUTINGKEY01);
//    }
//
//    @Bean
//    public Binding directBinding02(){
//        return BindingBuilder.bind(directQueue02()).to(directExchange()).with(ROUTINGKEY02);
//    }
}
