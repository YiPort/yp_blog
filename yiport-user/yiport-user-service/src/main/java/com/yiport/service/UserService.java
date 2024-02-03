package com.yiport.service;

import com.yiport.domain.ResponseResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yiport.domain.entity.User;
import com.yiport.domain.vo.EditUserVO;
import com.yiport.domain.vo.OtherUserVO;
import com.yiport.domain.vo.UserVO;

import java.util.List;


/**
 * 用户表(User)表服务接口
 *
 * @author yiport
 * @since 2023-05-01 13:50:17
 */
public interface UserService extends IService<User> {

    /**
     * 更新个人信息
     *
     * @param editUserVO
     * @return
     */
    ResponseResult updateUserInfo(EditUserVO editUserVO);


    /**
     * 获取用户登录态
     *
     * @return
     */
    ResponseResult getCurrent();

    /**
     * 管理员分页查询用户
     *
     * @param current  当前页
     * @param pageSize 页面容量
     * @return
     */
    ResponseResult<List<UserVO>> searchUsers(String current, String pageSize);

    /**
     * 管理员根据 id删除用户
     *
     * @param id
     * @return
     */
    ResponseResult deleteUserById(String id);

    ResponseResult<OtherUserVO> getOtherUser(Long userId);
}

