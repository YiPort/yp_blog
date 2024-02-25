package com.yiport.service;

import com.yiport.domain.ResponseResult;
import com.yiport.domain.request.AccountLoginRequest;
import com.yiport.domain.request.EmailLoginRequest;
import com.yiport.domain.request.UserRegisterRequest;


import java.util.Map;

public interface UserLoginService
{
    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    ResponseResult<Void> userRegister(UserRegisterRequest userRegisterRequest);

    /**
     * 用户登出
     *
     * @return
     */
    ResponseResult<Void> logout();

    /**
     * 账号密码登录
     *
     * @param accountLoginRequest 用户登录请求体
     * @return 结果
     */
    ResponseResult<Map<String, Object>> userLoginByAccount(AccountLoginRequest accountLoginRequest);

    /**
     * 邮箱验证码登录
     *
     * @param emailLoginRequest 邮箱验证码登录请求体
     * @return 结果
     */
    ResponseResult<Map<String, Object>> userLoginByEmail(EmailLoginRequest emailLoginRequest);

    /**
     * 获取图片验证码
     *
     * @return
     */
    ResponseResult getCaptchaImage();


}