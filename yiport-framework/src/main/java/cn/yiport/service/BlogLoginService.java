package cn.yiport.service;

import cn.yiport.domain.ResponseResult;
import cn.yiport.domain.entity.User;

public interface BlogLoginService {
    ResponseResult login(User user);

    ResponseResult logout();
}