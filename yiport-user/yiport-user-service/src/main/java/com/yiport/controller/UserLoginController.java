package com.yiport.controller;

import com.yiport.annotation.SystemLog;
import com.yiport.domain.ResponseResult;
import com.yiport.domain.request.AccountLoginRequest;
import com.yiport.enums.AppHttpCodeEnum;
import com.yiport.exception.SystemException;
import com.yiport.service.UserLoginService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserLoginController {
    @Autowired
    private UserLoginService userLoginService;

    /**
     * 用户登录
     *
     * @param accountLoginRequest
     * @return
     */
    @PostMapping("/login")
    @SystemLog(businessName = "用户登录")
    public ResponseResult login(@RequestBody AccountLoginRequest accountLoginRequest) {
        String userName = accountLoginRequest.getUserName();
        String userPassword = accountLoginRequest.getPassword();
        String captcha = accountLoginRequest.getCaptcha();
        String uuid = accountLoginRequest.getUuid();
        if (StringUtils.isBlank(userName)) {
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
        }
        if (StringUtils.isBlank(userPassword)){
            throw new SystemException(AppHttpCodeEnum.PASSWORD_NOT_NULL);
        }
        if (StringUtils.isBlank(captcha)){
            throw new SystemException(AppHttpCodeEnum.CAPTCHA_NOT_NULL);
        }
        if (StringUtils.isBlank(uuid)){
            throw new SystemException(AppHttpCodeEnum.CAPTCHA_NOT_NULL);
        }

        return userLoginService.userLoginByAccount(accountLoginRequest);
    }

    /**
     * 用户退出登录
     *
     * @return
     */
    @PostMapping("/logout")
    @SystemLog(businessName = "用户退出登录")
    public ResponseResult logout() {
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