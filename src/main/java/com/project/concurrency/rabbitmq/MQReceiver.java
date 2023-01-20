package com.project.concurrency.rabbitmq;

import com.project.concurrency.pojo.SecKillMessage;
import com.project.concurrency.pojo.SeckillOrder;
import com.project.concurrency.pojo.User;
import com.project.concurrency.service.IGoodsService;
import com.project.concurrency.service.IOrderService;
import com.project.concurrency.utils.JsonUtil;
import com.project.concurrency.utils.vo.GoodsVo;
import com.project.concurrency.utils.vo.RespBean;
import com.project.concurrency.utils.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class MQReceiver {
    @Resource
    private IGoodsService goodsService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private IOrderService orderService;

    @RabbitListener(queues = "seckillQueue")
    public void receiveOrders(String message){
        log.info("接受到的订单消息"+message);
        SecKillMessage secKillMessage = JsonUtil.jsonStr2Object(message, SecKillMessage.class);
        Long goodsId = secKillMessage.getGoodsId();
        User user = secKillMessage.getUser();
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);

        if(goodsVo.getStockCount()<1){return;} // 库存已经被消耗完了

        // 判断是否重复抢购 (在真正下单之前还是要判断一下)
        SeckillOrder seckillOrder =
                (SeckillOrder) redisTemplate.opsForValue().get("order:"+user.getId()+":"+goodsId);
        if(seckillOrder!=null){return;}//已经抢购过

        //下单 (更改持久层)
        orderService.secKill(user,goodsVo);



    }
//    @RabbitListener(queues = "queue")
//    public void receive(Object msg){
//        log.info("Received message: "+msg);
//    }
//
//    @RabbitListener(queues = "queue_fanout01")
//    public void receive_fanout01(Object msg){
//        log.info("1Received message: "+msg);
//    }
//
//    @RabbitListener(queues = "queue_fanout02")
//    public void receive_fanout02(Object msg){
//        log.info("2Received message: "+msg);
//    }
//
//    @RabbitListener(queues = "queue_direct01")
//    public void receive_direct01(Object msg){
//        log.info(" queue_direct01 Received message: "+msg);
//    }
//
//    @RabbitListener(queues = "queue_direct02")
//    public void receive_direct02(Object msg){
//        log.info(" queue_direct02 Received message: "+msg);
//    }
}
