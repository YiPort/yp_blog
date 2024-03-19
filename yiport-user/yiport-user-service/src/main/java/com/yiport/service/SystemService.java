package com.yiport.service;

import com.yiport.domain.ResponseResult;
import com.yiport.domain.entity.LoginInfo;
import com.yiport.domain.request.LoginInfoRequest;
import com.yiport.domain.request.UserRequest;
import com.yiport.domain.vo.PageVO;
import com.yiport.domain.vo.UserVO;

/**
 * 系统 服务层
 */
public interface SystemService
{

    /**
     * 管理员分页查询用户
     */
    ResponseResult<PageVO<UserVO>> selectPageUser(UserRequest userRequest);

    /**
     * 管理员根据 id删除用户
     */
    ResponseResult<Void> deleteUserById(Long id);

    /**
     * 管理员分页查询访问信息
     */
    ResponseResult<PageVO<LoginInfo>> selectPageLoginInfo(LoginInfoRequest loginInfoRequest);

    /**
     * 管理员删除访问信息
     */
    ResponseResult<Void> deleteLoginInfo(Long id);

}
