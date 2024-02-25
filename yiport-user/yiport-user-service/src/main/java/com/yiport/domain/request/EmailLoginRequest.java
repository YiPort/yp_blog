package com.yiport.domain.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static com.yiport.constent.UserConstant.MAIL_CAPTCHA_LENGTH;

/**
 * 邮箱验证码登录请求体
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class EmailLoginRequest extends BaseLoginRequest
{
    private static final long serialVersionUID = 1001507867319913941L;

    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "请输入正确的邮箱地址")
    private String email;

    /**
     * 邮箱验证码
     */
    @NotBlank(message = "验证码不能为空")
    @Size(max = MAIL_CAPTCHA_LENGTH, min = MAIL_CAPTCHA_LENGTH, message = "验证码错误")
    private String captcha;

}
