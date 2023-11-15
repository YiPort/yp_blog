package com.yiport.service;

import com.yiport.domain.ResponseResult;
import com.yiport.domain.entity.User;
import com.yiport.domain.request.AccountLoginRequest;
import com.yiport.domain.request.UserRegisterRequest;
import com.yiport.domain.vo.UserLoginVO;
import com.yiport.enums.AppHttpCodeEnum;

public interface UserLoginService {

    /**
     * 用户注册（停用）
     *
     * @param user
     * @return
     */
    @Deprecated
    ResponseResult<AppHttpCodeEnum> register(User user);

    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    ResponseResult<AppHttpCodeEnum> userRegister(UserRegisterRequest userRegisterRequest);

    /**
     * 用户登录（停用）
     *
     * @param user
     * @return
     */
    @Deprecated
    ResponseResult<UserLoginVO> login(User user);

    /**
     * 用户登出
     *
     * @return
     */
    ResponseResult<AppHttpCodeEnum> logout();

    /**
     * 账号密码登录
     *
     * @param accountLoginRequest 用户登录请求体
     * @return 结果
     */
    ResponseResult<UserLoginVO> userLoginByAccount(AccountLoginRequest accountLoginRequest);

    /**
     * 获取图片验证码
     *
     * @return
     */
    ResponseResult getCaptchaImage();


}