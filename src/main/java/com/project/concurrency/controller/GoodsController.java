package com.project.concurrency.controller;


import com.project.concurrency.pojo.User;
import com.project.concurrency.service.IGoodsService;
import com.project.concurrency.service.IUserService;
import com.project.concurrency.service.impl.UserServiceImpl;
import com.project.concurrency.utils.vo.GoodsVo;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;

@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Resource
    private IUserService userService;
    @Resource
    private IGoodsService goodsService;

//    @RequestMapping("/toList")
//    public String toList(HttpServletRequest request, HttpServletResponse response, Model model, @CookieValue("userTicket") String ticket){
//        /*以下可以省略*/
//        if(ticket.isEmpty()) return "login";
//        //User user = (User) session.getAttribute(ticket);  //ticket已经存到了浏览器, 现在通过ticket来获得session中的登录对象
//        User user = userService.getUserByCookie(ticket, request, response);  // 使用redis
//        if(user==null) return "login";
//        /*以上*/
//        model.addAttribute("user",user);
//        return "goodsList";
//    }
    @RequestMapping("/toList")
    public String toList(Model model, User user){
        /*以下可以省略*/
        if(user==null) return "login";
        /*以上*/
        model.addAttribute("user",user);
        model.addAttribute("goodsList",goodsService.findGoodsVo());
        return "goodsList";
    }

    @RequestMapping("/toDetail/{goodsID}")
    public String toDetail(Model model, User user, @PathVariable long goodsID){
        model.addAttribute("user",user);

        /*获取goodsVo, 然后从后端获取当前时间*/
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsID);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date currentDate = new Date();

        int secKillStatus = -1; // 表示
        int remainSeconds = -1; // 表示
        if(startDate.after(currentDate)){
            secKillStatus = 0; // 尚未开始
            remainSeconds = (int)((startDate.getTime()-currentDate.getTime())/1000);
        }
        else if(endDate.before(currentDate)){
            secKillStatus =  2;  // 已经结束
        }
        else{
            // 中间
            secKillStatus = 1;  // 正在进行中
            remainSeconds = 0;
        }
        model.addAttribute("goods", goodsVo);
        model.addAttribute("secKillStatus",secKillStatus);
        model.addAttribute("remainSeconds",remainSeconds);

        return "goodsDetail";
    }

}
