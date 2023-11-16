package com.yiport.domain.request;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 账号密码登录请求体
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AccountLoginRequest extends BaseLoginRequest {

    private static final long serialVersionUID = 1449842852532846833L;

    /**
     * 账号
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 验证码
     */
    private String captcha;

    /**
     * UUID
     */
    private String uuid;

}
