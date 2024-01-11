package com.yiport.client;

import com.yiport.domain.ResponseResult;
import com.yiport.domain.request.UserRegisterRequest;
import com.yiport.domain.vo.UserVO;
import com.yiport.enums.AppHttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * UserClient请求失败时的熔断处理类
 */
@Slf4j
@Component
public class UserClientResolver implements UserClient{

    /**
     * 当服务不可用时，为根据ID获取用户昵称提供默认响应。
     *
     * @param id 用户ID
     * @return 返回一个空用户，表示无法查询到用户信息。
     */
    @Override
    public UserVO getUserNickNameById(Long id) {
        log.error("用户服务异常：获取用户ID {} 的昵称请求失败", id);
        return new UserVO(); // Assuming the default constructor sets an empty or null nickname.
    }

    /**
     * 当服务不可用时，为更新用户信息提供默认响应。
     *
     * @param userVO 用户视图对象
     * @return 表示系统错误的错误结果。
     */
    @Override
    public ResponseResult updateUserInfo(UserVO userVO) {
        log.error("用户服务异常：更新用户信息请求失败");
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR, "用户服务不可用");
    }

    /**
     * 当服务不可用时，为用户注册提供默认响应。
     *
     * @param userRegisterRequest 用户注册请求对象
     * @return 表示系统错误的错误结果。
     */
    @Override
    public ResponseResult userRegister(UserRegisterRequest userRegisterRequest) {
        log.error("用户服务异常：用户注册请求失败");
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR, "用户服务不可用");
    }

    /**
     * 当服务不可用时，为获取当前用户登录状态提供默认响应。
     *
     * @return 表示系统错误的错误结果。
     */
    @Override
    public ResponseResult current() {
        log.error("用户服务异常：获取当前用户登录状态请求失败");
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR, "用户服务不可用");
    }

    /**
     * 当服务不可用时，为管理员根据昵称查询用户提供默认响应。
     *
     * @param username 用户昵称
     * @return 表示系统错误的错误结果。
     */
    @Override
    public ResponseResult<List<UserVO>> searchByUsername(String username) {
        log.error("用户服务异常：根据用户名 {} 查询用户请求失败", username);
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR, "用户服务不可用");
    }

    /**
     * 当服务不可用时，为管理员分页查询用户提供默认响应。
     *
     * @param current 当前页
     * @param pageSize 页面容量
     * @return 表示系统错误的错误结果。
     */
    @Override
    public ResponseResult<List<UserVO>> searchUsers(String current, String pageSize) {
        log.error("用户服务异常：分页查询用户请求失败");
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR, "用户服务不可用");
    }

    /**
     * 当服务不可用时，为管理员根据ID删除用户提供默认响应。
     *
     * @param id 用户ID
     * @return 表示系统错误的错误结果。
     */
    @Override
    public ResponseResult deleteUser(String id) {
        log.error("用户服务异常：删除用户ID {} 的请求失败", id);
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR, "用户服务不可用");
    }

    /**
     * 当服务不可用时，为获取图片验证码提供默认响应。
     *
     * @return 表示系统错误的错误结果。
     */
    @Override
    public ResponseResult getCaptchaImage() {
        log.error("用户登录服务异常：获取图片验证码请求失败");
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR, "验证码服务不可用");
    }

}
