package com.project.concurrency.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.concurrency.pojo.Order;
import com.project.concurrency.pojo.User;
import com.project.concurrency.utils.vo.GoodsVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhoubin
 * @since 2022-11-15
 */
public interface IOrderService extends IService<Order> {
    Order secKill(User user, GoodsVo goods);
}
