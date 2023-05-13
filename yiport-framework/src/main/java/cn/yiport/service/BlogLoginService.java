package cn.yiport.service;

import cn.yiport.domain.ResponseResult;
import cn.yiport.domain.entity.User;
import cn.yiport.domain.request.AccountLoginRequest;

public interface BlogLoginService {
    ResponseResult login(User user);

    ResponseResult logout();

    ResponseResult userLoginByAccount(AccountLoginRequest accountLoginRequest);

    ResponseResult getCaptchaImage();

}