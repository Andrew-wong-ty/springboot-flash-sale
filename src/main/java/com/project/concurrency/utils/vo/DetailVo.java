package com.project.concurrency.utils.vo;

import com.project.concurrency.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailVo {
    private User user;
    private GoodsVo goodsVo;
    private int SecSkillStatus;
    private int RemainSeconds;
}
