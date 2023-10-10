package com.yiport.client;

import com.yiport.domain.ResponseResult;
import com.yiport.domain.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value="user-service",fallback = UserClientResolver.class)
public interface UserClient {

//    /**
//     * 根据id返回user
//     * @param id
//     * @return
//     */
//    @GetMapping("/user/getUserById")
//    User getById(Long id);

//    /**
//     * 个人信息查询
//     * 需要token请求头
//     * @return
//     */
//    @GetMapping("/user/userInfo")
//    ResponseResult userInfo();

//    /**
//     * 更新个人信息
//     * 需要token请求头
//     * @param user
//     * @return
//     */
//    @PutMapping("/user/userInfo")
//    ResponseResult updateUserInfo(@RequestBody User user);


}