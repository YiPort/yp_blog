package com.yiport.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginVO {

    /**
     * Token
     */
    private String token;

    /**
     * 用户信息
     */
    private UserVO userInfo;
}