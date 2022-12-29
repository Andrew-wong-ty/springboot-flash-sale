package com.project.concurrency.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.concurrency.mapper.OrderMapper;
import com.project.concurrency.mapper.SeckillOrderMapper;
import com.project.concurrency.pojo.Order;
import com.project.concurrency.pojo.SeckillGoods;
import com.project.concurrency.pojo.SeckillOrder;
import com.project.concurrency.pojo.User;
import com.project.concurrency.service.IOrderService;
import com.project.concurrency.service.ISeckillGoodsService;
import com.project.concurrency.service.ISeckillOrderService;
import com.project.concurrency.utils.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * <p>
 * 秒杀订单表 服务实现类
 * </p>
 *
 * @author zhoubin
 * @since 2022-11-15
 */
@Service
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder> implements ISeckillOrderService {

}
