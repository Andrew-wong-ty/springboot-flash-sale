package com.project.concurrency.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.concurrency.mapper.GoodsMapper;
import com.project.concurrency.pojo.Goods;
import com.project.concurrency.service.IGoodsService;
import com.project.concurrency.utils.vo.GoodsVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhoubin
 * @since 2022-11-15
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {
    @Resource
    private GoodsMapper goodsMapper;
    @Override
    public List<GoodsVo> findGoodsVo() {
        return goodsMapper.findGoodsVo();
    }

    /**
     * 获取商品详情
     * */
    @Override
    public GoodsVo findGoodsVoByGoodsId(long goodsID) {
        return goodsMapper.findGoodsVoByGoodsId(goodsID);
    }
}
