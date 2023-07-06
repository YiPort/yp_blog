package com.yiport.service;

import com.yiport.domain.ResponseResult;
import com.yiport.domain.entity.User;
import com.yiport.domain.request.AccountLoginRequest;
import com.yiport.domain.request.UserRegisterRequest;
import com.yiport.domain.vo.UserLoginVO;
import com.yiport.enums.AppHttpCodeEnum;

public interface UserLoginService {

    ResponseResult<AppHttpCodeEnum> register(User user);

    ResponseResult userRegister(UserRegisterRequest userRegisterRequest);

    ResponseResult<UserLoginVO> login(User user);

    ResponseResult<AppHttpCodeEnum> logout();

    ResponseResult<UserLoginVO> userLoginByAccount(AccountLoginRequest accountLoginRequest);

    ResponseResult getCaptchaImage();


}