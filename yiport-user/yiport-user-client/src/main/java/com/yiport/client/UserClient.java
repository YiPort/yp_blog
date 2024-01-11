package com.yiport.client;

import com.yiport.domain.ResponseResult;
import com.yiport.domain.request.AccountLoginRequest;
import com.yiport.domain.request.UserRegisterRequest;
import com.yiport.domain.vo.UserVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Import(UserClientResolver.class)
@FeignClient(value="user-service",fallback = UserClientResolver.class)
public interface UserClient {

    /**
     * 根据ID获取用户昵称。
     *
     * @param id 用户ID。
     * @return 用户信息。
     */
    @GetMapping("/user/getUserNickNameById")
    UserVO getUserNickNameById(@RequestParam Long id);

    /**
     * 更新个人信息。
     *
     * @param userVO 用户信息对象。
     * @return 操作结果。
     */
    @PutMapping("/user/saveUserInfo")
    ResponseResult updateUserInfo(@RequestBody UserVO userVO);

    /**
     * 用户注册。
     *
     * @param userRegisterRequest 用户注册请求。
     * @return 操作结果。
     */
    @PostMapping("/user/register")
    ResponseResult userRegister(@RequestBody UserRegisterRequest userRegisterRequest);

    /**
     * 获取用户登录态。
     *
     * @return 登录态信息。
     */
    @GetMapping("/user/current")
    ResponseResult current();

    /**
     * 管理员根据用户昵称查询用户。
     *
     * @param username 用户昵称。
     * @return 包含用户信息的响应结果。
     */
    @GetMapping("/user/searchByUsername")
    ResponseResult<List<UserVO>> searchByUsername(@RequestParam("username") String username);

    /**
     * 管理员分页查询用户。
     *
     * @param current 当前页。
     * @param pageSize 页面容量。
     * @return 包含用户列表的响应结果。
     */
    @GetMapping("/user/searchUsers")
    ResponseResult<List<UserVO>> searchUsers(@RequestParam("current") String current, @RequestParam("pageSize") String pageSize);

    /**
     * 管理员根据ID删除用户。
     *
     * @param id 用户ID。
     * @return 操作结果。
     */
    @PostMapping("/user/delete")
    ResponseResult deleteUser(@RequestParam("id") String id);


    /**
     * 获取图片验证码。
     *
     * @return 验证码图片。
     */
    @GetMapping("/user/captchaImage")
    ResponseResult getCaptchaImage();
}