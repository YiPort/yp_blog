package com.yiport.controller;

import com.yiport.annotation.LimitRequest;
import com.yiport.annotation.SystemLog;
import com.yiport.domain.ResponseResult;
import com.yiport.domain.request.AccountLoginRequest;
import com.yiport.domain.request.UserRegisterRequest;
import com.yiport.exception.SystemException;
import com.yiport.service.UserLoginService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.yiport.constants.SystemConstants.DEFINED;

@RestController
@RequestMapping("/user")
public class UserLoginController {
    @Autowired
    private UserLoginService userLoginService;

    /**
     * 用户注册
     * 不需要token请求头
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    @LimitRequest(time = 12 * 60 * 60 * 1000, description = "操作频繁，休息一下吧~", tip = DEFINED)
    public ResponseResult<Void> userRegister(@Validated @RequestBody UserRegisterRequest userRegisterRequest)
    {
         return userLoginService.userRegister(userRegisterRequest);
    }

    /**
     * 用户登录
     *
     * @param accountLoginRequest
     * @return
     */
    @PostMapping("/login")
    @SystemLog(businessName = "用户登录")
    public ResponseResult login(@RequestBody AccountLoginRequest accountLoginRequest)
    {
        return userLoginService.userLoginByAccount(accountLoginRequest);
    }

    /**
     * 用户退出登录
     *
     * @return
     */
    @PostMapping("/logout")
    @SystemLog(businessName = "用户退出登录")
    public ResponseResult<Void> logout() {
        return userLoginService.logout();
    }

    /**
     * 获取图片验证码
     *
     * @return
     */
    @GetMapping("/captchaImage")
    @SystemLog(businessName = "获取图片验证码")
    public ResponseResult getCaptchaImage() {
        return userLoginService.getCaptchaImage();
    }

}