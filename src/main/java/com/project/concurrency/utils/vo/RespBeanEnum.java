package com.project.concurrency.utils.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/*
* 公共返回对象的枚举
* 里面包括状态码以及一些常用的状态提示
* */
@Getter
@ToString
@AllArgsConstructor
public enum RespBeanEnum {
    SUCCESS(200,"SUCCESS"),
    ERROR(500, "client error"),
    LOGIN_ERROR(500210,"用户名或密码错误"),
    MOBILE_ERROR(500211,"手机号码格式不正确"),
    STOCK_ERROR(500212,"库存不足"),
    REPEATED_ORDER_ERROR(500213,"已经抢过此商品"),
    USER_NOT_FOUND_ERROR(500214,"用户不存在"),
    EMPTY_STOCK(50021,"库存不足"),
    REPEAT_PURCHASE(50021,"重复购买");
    private final Integer code;
    private final String message;
}
