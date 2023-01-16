package com.project.concurrency.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.concurrency.mapper.UserMapper;
import com.project.concurrency.pojo.User;
import com.project.concurrency.service.IUserService;
import com.project.concurrency.utils.CookieUtil;
import com.project.concurrency.utils.MD5Util;
import com.project.concurrency.utils.UUIDUtil;
import com.project.concurrency.utils.ValidatorUtil;
import com.project.concurrency.utils.vo.LoginVo;
import com.project.concurrency.utils.vo.RespBean;
import com.project.concurrency.utils.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhoubin
 * @since 2022-11-13
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private RedisTemplate redisTemplate;


    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        /*登录逻辑*/
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        /*首先判断格式是否正确*/
        if(mobile.isEmpty() || password.isEmpty()){
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        if(!ValidatorUtil.isMobile(mobile)){
            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
        }
        User user = null;
        try {
            user  = userMapper.selectById(mobile);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        if(user==null){
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        // 判断密码是否正确
        if(!MD5Util.formPassToDBPass(password,user.getSalt()).equals(user.getPassword())){
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        // 生成cookie
        String ticket = UUIDUtil.uuid();
        //request.getSession().setAttribute(ticket, user); // 把刚刚成功登录的user对象(明文!但密码已经加密了)放到session中

        redisTemplate.opsForValue().set("user:"+ticket,user); // 存到redis中, 而不是session中
        log.info("generate cookie= "+ticket);
//        CookieUtil.setCookie(request,response,"userTicket", ticket);
        CookieUtil.wongSetCookie(response,"userTicket", ticket);
//        /*测试添加cookie*/
//        Cookie cookie = new Cookie("name", "WONG");
//        cookie.setPath("/");
//        cookie.setMaxAge(365 * 24 * 60 * 60);
//        response.addCookie(cookie);
        return RespBean.success(ticket);
    }


    /**
     * 通过存在本地的cookie来获取redis中的登录对象
     * @param ticket
     * @return
     */
    @Override
    public User getUserByCookie(String ticket,HttpServletRequest request, HttpServletResponse response) {
        if(ticket.isEmpty() || ticket.equals("null")) return null;
        User user = (User)redisTemplate.opsForValue().get("user:" + ticket); //会获取到一个用户对象, 因此强转
        // 如果非空, 则说明用户以及登录过了, 以防止cookie过期, 重新设置一下cookie
        //if(user!=null) CookieUtil.setCookie(request,response,"userTicket", ticket);
        if(user!=null) CookieUtil.wongSetCookie(response,"userTicket", ticket);

        return user;
    }

    /**
     * 对象缓存
     * @param userTicket
     * @param password
     * @param request
     * @param response
     * @return
     */
    @Override
    public RespBean updatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response) {
        // 首先更新数据库
        User user = getUserByCookie(userTicket,request, response);
        user.setPassword(MD5Util.inputPassToDBPass(password, user.getSalt()));
        int result = userMapper.updateById(user);
        // 然后删除redis中的缓存
        if(result==1){
            redisTemplate.delete("user:"+userTicket);// delete the older version of "user" cached in redis.
            return RespBean.success();
        }
        return RespBean.error(RespBeanEnum.LOGIN_ERROR);
    }
}
