package com.project.concurrency.controller;


import com.project.concurrency.pojo.User;
import com.project.concurrency.service.IGoodsService;
import com.project.concurrency.service.IUserService;
import com.project.concurrency.service.impl.UserServiceImpl;
import com.project.concurrency.utils.vo.DetailVo;
import com.project.concurrency.utils.vo.GoodsVo;
import com.project.concurrency.utils.vo.RespBean;
import io.netty.util.internal.StringUtil;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Resource
    private IUserService userService;
    @Resource
    private IGoodsService goodsService;
    @Resource
    // 引入 redis
    private RedisTemplate redisTemplate;
    @Resource
    private ThymeleafViewResolver thymeleafViewResolver; // 用于手动渲染thymeleaf

    /**
     * 以下是未使用页面缓存的代码
     */
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
//    @RequestMapping("/toList")
//    public String toList(Model model, User user){
//        /*以下可以省略*/
//        if(user==null) return "login";
//        /*以上*/
//        model.addAttribute("user",user);
//        model.addAttribute("goodsList",goodsService.findGoodsVo());
//        return "goodsList";
//    }

    /**
     * user对象是自动根据cookie来获取的
     * @param model
     * @param user
     * @return
     */
    @RequestMapping(value = "/toList", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toList(Model model, User user,
                         HttpServletRequest request, HttpServletResponse response
        ){
        /*以下可以省略*/
        if(user==null) return "login";
        /*以上*/

        // 在redis中尝试读取页面缓存 START1
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsList");
        if (!StringUtils.isEmpty(html)){
            return html;
        }
        // 在redis中尝试读取页面缓存 END1


        model.addAttribute("user",user);
        model.addAttribute("goodsList",goodsService.findGoodsVo());  //??
//        return "goodsList"; // 不单只做页面跳转

        // 手动渲染, 并写入redis缓存 START
        IWebContext iWebContext = new WebContext(request, response, request.getServletContext(), request.getLocale(),model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList",iWebContext);
        if(!StringUtils.isEmpty(html)){
            // html不为空, 则把html写入到redis中
            valueOperations.set("goodsList",html,60, TimeUnit.SECONDS);
        }
        return html;
        // 手动渲染, 并写入redis缓存 END
    }


    /**
     * 页面静态化(前后端分离的页面)
     * @param model
     * @param user
     * @param goodsID
     * @return
     */
    @RequestMapping("/detail/{goodsID}")
    @ResponseBody
    public RespBean toDetail(Model model, User user, @PathVariable long goodsID){


//        model.addAttribute("user",user);
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
//        model.addAttribute("goods", goodsVo);
//        model.addAttribute("secKillStatus",secKillStatus);
//        model.addAttribute("remainSeconds",remainSeconds);
        // 不再用model, 改用页面静态化
        DetailVo detailVo = new DetailVo();
        detailVo.setUser(user);
        detailVo.setGoodsVo(goodsVo);
        detailVo.setSecSkillStatus(secKillStatus);
        detailVo.setRemainSeconds(remainSeconds);

        return RespBean.success(detailVo);


    }

    /**
     * 使用了页面缓存的toDetail页面
     * @param model
     * @param user
     * @param goodsID
     * @param request
     * @param response
     * @return
     */
//    @RequestMapping(value="/toDetail/{goodsID}", produces = "text/html;charset=utf-8")
//    @ResponseBody
//    public String toDetail(Model model, User user, @PathVariable long goodsID,
//                           HttpServletRequest request, HttpServletResponse response
//    ){
//        ValueOperations valueOperations = redisTemplate.opsForValue(); // 用于进行HTML操作
//
//        // 在redis中尝试读取页面缓存 START
//        String html = (String) valueOperations.get("goodsDetail"+goodsID);
//        if(!StringUtils.isEmpty(html)){
//            //若html非空, 则说明有提前缓存
//            return html;
//        }
//        // 在redis中尝试读取页面缓存 END
//
//        model.addAttribute("user",user);
//        /*获取goodsVo, 然后从后端获取当前时间*/
//        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsID);
//        Date startDate = goodsVo.getStartDate();
//        Date endDate = goodsVo.getEndDate();
//        Date currentDate = new Date();
//
//        int secKillStatus = -1; // 表示
//        int remainSeconds = -1; // 表示
//        if(startDate.after(currentDate)){
//            secKillStatus = 0; // 尚未开始
//            remainSeconds = (int)((startDate.getTime()-currentDate.getTime())/1000);
//        }
//        else if(endDate.before(currentDate)){
//            secKillStatus =  2;  // 已经结束
//        }
//        else{
//            // 中间
//            secKillStatus = 1;  // 正在进行中
//            remainSeconds = 0;
//        }
//        model.addAttribute("goods", goodsVo);
//        model.addAttribute("secKillStatus",secKillStatus);
//        model.addAttribute("remainSeconds",remainSeconds);
//
//        // 手动渲染, 并写入redis缓存 START
//        IWebContext iWebContext = new WebContext(request, response, request.getServletContext(), request.getLocale(),model.asMap());
//        html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail",iWebContext);
//        if(!StringUtils.isEmpty(html)){
//            // html不为空, 则把html写入到redis中
//            valueOperations.set("goodsDetail"+goodsID,html,60, TimeUnit.SECONDS);
//        }
//        return html;
//        // 手动渲染, 并写入redis缓存 END
//
//    }


    /**
     * 以下是未使用页面华缓存的代码
     */
//    @RequestMapping("/toDetail/{goodsID}")
//    public String toDetail(Model model, User user, @PathVariable long goodsID){
//
//        model.addAttribute("user",user);
//        /*获取goodsVo, 然后从后端获取当前时间*/
//        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsID);
//        Date startDate = goodsVo.getStartDate();
//        Date endDate = goodsVo.getEndDate();
//        Date currentDate = new Date();
//
//        int secKillStatus = -1; // 表示
//        int remainSeconds = -1; // 表示
//        if(startDate.after(currentDate)){
//            secKillStatus = 0; // 尚未开始
//            remainSeconds = (int)((startDate.getTime()-currentDate.getTime())/1000);
//        }
//        else if(endDate.before(currentDate)){
//            secKillStatus =  2;  // 已经结束
//        }
//        else{
//            // 中间
//            secKillStatus = 1;  // 正在进行中
//            remainSeconds = 0;
//        }
//        model.addAttribute("goods", goodsVo);
//        model.addAttribute("secKillStatus",secKillStatus);
//        model.addAttribute("remainSeconds",remainSeconds);
//
//        return "goodsDetail";
//    }

}
