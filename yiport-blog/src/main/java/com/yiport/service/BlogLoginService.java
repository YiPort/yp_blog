package com.yiport.service;

import com.yiport.domain.ResponseResult;
import com.yiport.domain.entity.User;
import com.yiport.domain.request.AccountLoginRequest;

public interface BlogLoginService {
    ResponseResult login(User user);

    ResponseResult logout();

    ResponseResult userLoginByAccount(AccountLoginRequest accountLoginRequest);

    ResponseResult getCaptchaImage();

}