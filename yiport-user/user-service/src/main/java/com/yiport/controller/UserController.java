package com.yiport.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yiport.annotation.SystemLog;
import com.yiport.domain.ResponseResult;
import com.yiport.domain.entity.User;
import com.yiport.domain.vo.UserVO;
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
    public ResponseResult register(@RequestBody User user) {
        return userService.register(user);
    }


    /**
     * 获取用户登录态
     *
     * @return
     */
    @GetMapping("/current")
    public ResponseResult<UserVO> current() {
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
    public ResponseResult<List<UserVO>> searchByUsername(String username) {
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
    public ResponseResult<List<UserVO>> searchUsers(Integer current, Integer pageSize) {
        return userService.searchUsers(current, pageSize);
    }

    /**
     * 管理员根据 id删除用户
     *
     * @param id
     * @return
     */
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('1')")
    public ResponseResult deleteUser(@RequestBody Long id) {
        return userService.deleteUserById(id);
    }


}