package com.yiport.domain.request;

import lombok.Data;

import java.io.Serializable;


/**
 * 用户注册请求体
 */
@Data
public class UserRegisterRequest implements Serializable {
    private static final long serialVersionUID = 946359976113063279L;

    /**
     * 账号
     */
    private String userName;

    /**
     * 密码
     */
     private String password;

    /**
     * 校验密码
     */
    private String checkPassword;

    /**
     * 验证码
     */
    private String captcha;

    /**
     * 验证码UUID
     */
    private String uuid;

}
