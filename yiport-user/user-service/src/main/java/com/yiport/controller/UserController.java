package com.yiport.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yiport.annotation.SystemLog;
import com.yiport.domain.ResponseResult;
import com.yiport.domain.entity.User;
import com.yiport.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;


    /**
     * 根据id返回user
     * @param id
     * @return
     */
    @GetMapping("/getUserNickNameById")
    public User getById(Long id) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId, String.valueOf(id)).select(User::getNickName);

        User user = userService.getOne(queryWrapper);
        return user;
    }


    /**
     * 个人信息查询
     * 需要token请求头
     * @return
     */
    @GetMapping("/userInfo")
    public ResponseResult userInfo(){
        return userService.userInfo();
    }

    /**
     * 更新个人信息
     * 需要token请求头
     * @param user
     * @return
     */
    @PutMapping("/userInfo")
    @SystemLog(businessName = "更新个人信息")
    public ResponseResult updateUserInfo(@RequestBody User user){
        return userService.updateUserInfo(user);
    }

    /**
     * 用户注册
     * 不需要token请求头
     * @param user
     * @return
     */
    @PostMapping("/register")
    public ResponseResult register(@RequestBody User user){
        return userService.register(user);
    }

}