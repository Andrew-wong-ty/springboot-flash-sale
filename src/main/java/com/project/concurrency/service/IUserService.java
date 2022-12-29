package com.project.concurrency.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.concurrency.pojo.User;
import com.project.concurrency.utils.vo.LoginVo;
import com.project.concurrency.utils.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhoubin
 * @since 2022-11-13
 */
public interface IUserService extends IService<User> {

    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);

    User getUserByCookie(String ticket, HttpServletRequest request, HttpServletResponse response);
}
