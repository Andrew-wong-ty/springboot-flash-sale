package com.project.concurrency.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.concurrency.pojo.Goods;
import com.project.concurrency.utils.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhoubin
 * @since 2022-11-15
 */
public interface IGoodsService extends IService<Goods> {
    List<GoodsVo> findGoodsVo();


    /**
     * 获取商品详情
     * @param goodsID
     * @return
     */
    GoodsVo findGoodsVoByGoodsId(long goodsID);
}
