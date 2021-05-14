package com.myq.miaosha.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myq.miaosha.entity.User;
import com.myq.miaosha.redis.RedisService;
import com.myq.miaosha.result.Result;
import com.myq.miaosha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 孟赟强
 * @since 2021-05-05
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @RequestMapping("/info")
    @ResponseBody
    public Result<User> info(Model model, User user) {
        return Result.success(user);
    }

    /**
     * 查询所有用户
     */
    @RequestMapping("/back_list")
    @ResponseBody
    public String userList(){
        List<User> userList = userService.listUser();
        return JSON.toJSONString(userList);
    }

    /**
     * 查询单个用户
     */
    @RequestMapping("/edit/{id}")
    @ResponseBody
    public String user(@PathVariable("id") Long id){
        User user= userService.getById(id);
        return JSON.toJSONString(user);
    }

    /**
     * 分页查询用户
     * @return
     */
    @RequestMapping("/back_userinfo")
    @ResponseBody
    public IPage<User> users(HttpServletRequest request){
        //获取前台发送过来的数据
        Integer pageNo = Integer.valueOf(request.getParameter("pageNo"));
        Integer pageSize = Integer.valueOf(request.getParameter("pageSize"));
        IPage<User> page = new Page<>(pageNo, pageSize);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        User user = new User();
        wrapper.setEntity(user);
        return userService.page(page,wrapper);
    }
}
