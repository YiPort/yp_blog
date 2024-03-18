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
     * 修改用户信息
     *
     * @param editUserVO
     * @return
     */
    ResponseResult updateUserInfo(EditUserVO editUserVO);

    /**
     * 修改密码
     */
    ResponseResult<Void> updatePassword(EditUserVO editUserVO);


    /**
     * 获取用户登录态
     *
     * @return
     */
    ResponseResult<Object> getCurrent();

    /**
     * 获取其他用户信息
     */
    ResponseResult<OtherUserVO> getOtherUser(Long userId);
}

