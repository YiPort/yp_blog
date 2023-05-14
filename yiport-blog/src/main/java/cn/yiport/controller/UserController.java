package cn.yiport.controller;

import cn.yiport.annotation.SystemLog;
import cn.yiport.domain.ResponseResult;
import cn.yiport.domain.entity.User;
import cn.yiport.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

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
    @SystemLog(BusinessName = "更新个人信息")
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