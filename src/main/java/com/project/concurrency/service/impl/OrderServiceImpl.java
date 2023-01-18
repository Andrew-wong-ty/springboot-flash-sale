package com.project.concurrency.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.concurrency.mapper.OrderMapper;
import com.project.concurrency.pojo.Order;
import com.project.concurrency.pojo.SeckillGoods;
import com.project.concurrency.pojo.SeckillOrder;
import com.project.concurrency.pojo.User;
import com.project.concurrency.service.IGoodsService;
import com.project.concurrency.service.IOrderService;
import com.project.concurrency.service.ISeckillGoodsService;
import com.project.concurrency.service.ISeckillOrderService;
import com.project.concurrency.utils.vo.GoodsVo;
import com.project.concurrency.utils.vo.OrderDetailVo;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhoubin
 * @since 2022-11-15
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {
    @Resource
    private ISeckillGoodsService goodsService;
    @Resource
    private OrderMapper orderMapper; // 其实这里不规范, 这里应该调用service
    @Resource
    private ISeckillOrderService orderService;
    @Resource
    private IGoodsService iGoodsService;

    @Override
    public Order secKill(User user, GoodsVo goods) {
        // 减库存
        // 首先获取秒杀商品
        SeckillGoods seckillGoods = goodsService.getOne(
                new QueryWrapper<SeckillGoods>().eq("goods_id",goods.getId())
        );
        // 减库存
        seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
        goodsService.updateById(seckillGoods);

        // 生成订单, 然后插入到order表
        Order order = new Order();
        // order.setId();// id就不用生成了, 因为这是自增的
        order.setUserId(user.getId());
        order.setGoodsId(goods.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);

        // 生成秒杀订单 (有2个订单表, 第一个是大的order订单, 然后有一个秒杀订单表, 用于记录哪个订单是秒杀的)
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(goods.getId());
        orderService.save(seckillOrder); // 插入




        return order;
    }


    /**
     * 根据orderId来查询订单详情
     * @param orderId
     * @return
     */
    @Override
    public OrderDetailVo detail(Long orderId) {
        if(orderId==null){
            throw  new RuntimeException();  // 这里要永global exceptions
        }
        Order order = orderMapper.selectById(orderId);
        GoodsVo goodsVo = iGoodsService.findGoodsVoByGoodsId(order.getGoodsId());
        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setOrder(order);
        orderDetailVo.setGoodsVo(goodsVo);
        return orderDetailVo;
    }
}
