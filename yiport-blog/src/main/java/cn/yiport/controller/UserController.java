package cn.yiport.controller;

import cn.yiport.annotation.SystemLog;
import cn.yiport.domain.ResponseResult;
import cn.yiport.domain.entity.User;
import cn.yiport.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Api(tags = "用户管理",description = "用户管理相关接口")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 个人信息查询
     * 需要token请求头
     * @return
     */
    @GetMapping("/userInfo")
    @ApiOperation(value = "个人信息查询",notes = "获取登录用户的个人信息，需要token请求头")
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
    @SystemLog(BusinessName = "更新个人信息")
    @ApiOperation(value = "更新个人信息",notes = "更新登录用户的个人信息，需要token请求头")
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
    @ApiOperation(value = "注册用户",notes = "注册新用户，不需要token请求头")
    public ResponseResult register(@RequestBody User user){
        return userService.register(user);
    }

}