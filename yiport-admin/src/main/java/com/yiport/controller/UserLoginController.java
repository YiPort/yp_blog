package com.yiport.controller;

import com.yiport.domain.ResponseResult;
import com.yiport.domain.entity.User;
import com.yiport.domain.request.AccountLoginRequest;
import com.yiport.enums.AppHttpCodeEnum;
import com.yiport.handler.exception.SystemException;
import com.yiport.service.BlogLoginService;
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
    private UserLoginService loginService;

    /**
     * 用户登录
     * @param accountLoginRequest
     * @return
     */
    @PostMapping("/login1")
  public ResponseResult login( @RequestBody AccountLoginRequest accountLoginRequest){
        String userName = accountLoginRequest.getUserName();
        String userPassword = accountLoginRequest.getPassword();
        String captcha = accountLoginRequest.getCaptcha();
        String uuid = accountLoginRequest.getUuid();
        if (StringUtils.isBlank(userName) ) {
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

        return loginService.userLoginByAccount(accountLoginRequest);
    }

    /**
     * 用户退出登录
     * 需要token请求头
     * @return
     */
    @PostMapping("/logout")
    public ResponseResult logout(){
        return loginService.logout();
    }

    /**
     * 获取图片验证码
     *
     * @return
     */
    @GetMapping("/user/captchaImage")
    public ResponseResult getCaptchaImage() {
        return loginService.getCaptchaImage();
    }


    @PostMapping("/login")
    public ResponseResult login(@RequestBody User user){
        if(!org.springframework.util.StringUtils.hasText(user.getUserName())){
            //提示 必须要传用户名
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
        }
        return loginService.login(user);
    }

}