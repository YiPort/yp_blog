package com.yiport.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yiport.annotation.SystemLog;
import com.yiport.domain.ResponseResult;
import com.yiport.domain.entity.User;
import com.yiport.domain.request.UserRegisterRequest;
import com.yiport.domain.vo.UserVO;
import com.yiport.service.UserLoginService;
import com.yiport.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserLoginService userLoginService;


    /**
     * 根据id返回user
     *
     * @param id
     * @return
     */
    @GetMapping("/getUserNickNameById")
    @SystemLog(businessName = "根据id返回user")
    public User getById(Long id) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId, String.valueOf(id)).select(User::getNickName);

        User user = userService.getOne(queryWrapper);
        return user;
    }

    /**
     * 更新个人信息
     *
     * @param user
     * @return
     */
    @PutMapping("/saveUserInfo")
    @SystemLog(businessName = "更新个人信息")
    public ResponseResult updateUserInfo(@RequestBody UserVO userVO){
        return userService.updateUserInfo(userVO);
    }

    /**
     * 用户注册
     * 不需要token请求头
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public ResponseResult userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            return null;
        }

        return userLoginService.userRegister(userRegisterRequest);

    }


    /**
     * 获取用户登录态
     *
     * @return
     */
    @GetMapping("/current")
    @SystemLog(businessName = "获取用户登录态")
    public ResponseResult current() {
        return userService.getCurrent();
    }

    /**
     * 管理员根据用户昵称查询用户
     *
     * @param username 用户昵称
     * @return
     */
    @GetMapping("/searchByUsername")
    @PreAuthorize("hasAuthority('1')")
    @SystemLog(businessName = "管理员根据用户昵称查询用户")
    public ResponseResult<List<UserVO>> searchByUsername(@RequestParam("username") String username) {
        return userService.searchByUsername(username);
    }

    /**
     * 管理员分页查询用户
     *
     * @param current  当前页
     * @param pageSize 页面容量
     * @return
     */
    @GetMapping("/searchUsers")
    @PreAuthorize("hasAuthority('1')")
    @SystemLog(businessName = "管理员分页查询用户")
    public ResponseResult<List<UserVO>> searchUsers(@RequestParam("current") String current, @RequestParam("pageSize") String pageSize) {
        return userService.searchUsers(current, pageSize);
    }

    /**
     * 管理员根据id删除用户
     *
     * @param id
     * @return
     */
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('1')")
    @SystemLog(businessName = "管理员根据id删除用户")
    public ResponseResult deleteUser(@RequestParam("id") @RequestBody String id) {
        return userService.deleteUserById(id);
    }


}