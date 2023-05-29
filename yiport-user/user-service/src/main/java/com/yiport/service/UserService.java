package com.yiport.service;

import com.yiport.domain.ResponseResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yiport.domain.entity.User;
import com.yiport.domain.vo.UserVO;
import com.yiport.enums.AppHttpCodeEnum;

import java.util.List;


/**
 * 用户表(User)表服务接口
 *
 * @author yiport
 * @since 2023-05-01 13:50:17
 */
public interface UserService extends IService<User> {

    ResponseResult userInfo();

    ResponseResult updateUserInfo(User user);

    ResponseResult<AppHttpCodeEnum> register(User user);


    /**
     * 获取用户登录态
     *
     * @return
     */
    ResponseResult<UserVO> getCurrent();

    /**
     * 管理员根据用户昵称查询用户
     *
     * @param username 用户昵称
     * @return
     */
    ResponseResult<List<UserVO>> searchByUsername(String username);

    /**
     * 管理员分页查询用户
     *
     * @param current  当前页
     * @param pageSize 页面容量
     * @return
     */
    ResponseResult<List<UserVO>> searchUsers(Integer current, Integer pageSize);

    /**
     * 管理员根据 id删除用户
     *
     * @param id
     * @return
     */
    ResponseResult deleteUserById(Long id);

}

