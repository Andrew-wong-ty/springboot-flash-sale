package com.project.concurrency.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.concurrency.pojo.Order;
import com.project.concurrency.pojo.SeckillOrder;
import com.project.concurrency.pojo.User;
import com.project.concurrency.utils.vo.GoodsVo;

/**
 * <p>
 * 秒杀订单表 服务类
 * </p>
 *
 * @author zhoubin
 * @since 2022-11-15
 */
public interface ISeckillOrderService extends IService<SeckillOrder> {
    /**
     * 用于查询是否秒杀成功
     * @param user
     * @param goodsId
     * @return
     */
    Long getResult(User user, Long goodsId);
}
