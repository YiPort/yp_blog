package com.yiport.domain.request;

import lombok.Data;

/**
 * 邮箱验证请求体
 */
@Data
public class MailVerifyRequest
{
    private String email;

    private String captcha;
}
