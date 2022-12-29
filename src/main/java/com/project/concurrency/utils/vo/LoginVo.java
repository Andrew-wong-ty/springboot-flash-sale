package com.project.concurrency.utils.vo;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
@ToString
public class LoginVo {
    @NotNull
    private String mobile;

    @NotNull
    @Length(min=32)
    private String password;
}
