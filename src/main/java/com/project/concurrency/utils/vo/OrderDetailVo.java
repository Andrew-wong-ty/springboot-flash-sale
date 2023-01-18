package com.project.concurrency.utils.vo;

import com.project.concurrency.pojo.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailVo {
    private Order order;
    private  GoodsVo goodsVo;
}
