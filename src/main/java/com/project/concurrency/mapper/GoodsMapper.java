package com.project.concurrency.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.concurrency.pojo.Goods;
import com.project.concurrency.utils.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zhoubin
 * @since 2022-11-15
 */
public interface GoodsMapper extends BaseMapper<Goods> {

    List<GoodsVo> findGoodsVo();

    GoodsVo findGoodsVoByGoodsId(long goodsId);
}
