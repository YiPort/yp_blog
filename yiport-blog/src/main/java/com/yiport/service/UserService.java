package com.yiport.service;

import com.yiport.domain.ResponseResult;
import com.yiport.domain.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;


/**
 * 用户表(User)表服务接口
 *
 * @author yiport
 * @since 2023-05-01 13:50:17
 */
public interface UserService extends IService<User> {

    ResponseResult userInfo();

    ResponseResult updateUserInfo(User user);

    ResponseResult register(User user);

}

