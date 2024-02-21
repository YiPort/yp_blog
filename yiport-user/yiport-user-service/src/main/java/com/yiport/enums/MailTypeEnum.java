package com.yiport.enums;

/**
 * 邮件类型
 */
public enum MailTypeEnum
{
    UPDATE_MAIL("验证邮箱"),

    RETRIEVE_ACCOUNT("使用邮箱找回账号"),

    UPDATE_PASSWORD("使用邮箱修改密码");

    String type;

    MailTypeEnum(String type)
    {
        this.type = type;
    }

    public String getType()
    {
        return type;
    }

}
