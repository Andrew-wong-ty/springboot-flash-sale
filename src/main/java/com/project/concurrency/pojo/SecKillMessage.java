package com.project.concurrency.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 秒杀信息类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecKillMessage {
    private User user;
    private Long goodsId;

}
