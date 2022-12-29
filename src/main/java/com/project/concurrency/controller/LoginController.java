package com.project.concurrency.controller;

import com.project.concurrency.service.IUserService;
import com.project.concurrency.utils.vo.LoginVo;
import com.project.concurrency.utils.vo.RespBean;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.extern.slf4j.Slf4j; // 用于输出日志
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
@RequestMapping("/login")
@Slf4j
public class LoginController {
    @Autowired
    private IUserService userService;


    @RequestMapping("/toLogin")
    public String toLogin(){
        return "login";
    }


    @RequestMapping("/doLogin") // 返回公用的返回对象
    @ResponseBody
    public RespBean doLogin(@Validated LoginVo loginVo, HttpServletRequest request, HttpServletResponse response){
        log.info("{}",loginVo);
        RespBean respBean = userService.doLogin(loginVo, request, response);
        return respBean;
    }
//    @ResponseBody
//    @RequestMapping("/doLogin") // 返回公用的返回对象
//    public RespBean doLogin(Map<String,Object> map, HttpServletRequest request, HttpServletResponse response){
//        System.out.println(map);
//        return null;
//    }


}