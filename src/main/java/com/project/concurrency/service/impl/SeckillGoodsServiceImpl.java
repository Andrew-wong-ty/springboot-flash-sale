package com.project.concurrency.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.concurrency.mapper.SeckillGoodsMapper;
import com.project.concurrency.pojo.SeckillGoods;
import com.project.concurrency.service.ISeckillGoodsService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 秒杀商品表 服务实现类
 * </p>
 *
 * @author zhoubin
 * @since 2022-11-15
 */
@Service
public class SeckillGoodsServiceImpl extends ServiceImpl<SeckillGoodsMapper, SeckillGoods> implements ISeckillGoodsService {

}
