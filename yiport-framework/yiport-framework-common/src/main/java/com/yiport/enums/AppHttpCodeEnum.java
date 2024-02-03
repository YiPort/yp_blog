package com.yiport.enums;

public enum AppHttpCodeEnum {

    /**
     * 操作成功
     */
    SUCCESS(200,"操作成功"),
    /**
     * 请求参数错误*/
    PARAMETER_ERROR(400, "请求参数错误"),

    /**
     * 需要登录后操作
     */
    NEED_LOGIN(401,"需要登录后操作"),
    /**
     * 刷新Token
     */
    RELOAD_TOKEN(402, "刷新Token"),

    /**
     * 无权限操作
     */
    NO_OPERATOR_AUTH(403,"无权限操作"),

    /**
     * 找不到资源
     */
    SORRY_NOT_FOUND(404, "找不到资源"),

    /**
     * 系统错误
     */
    SYSTEM_ERROR(500,"系统错误"),

    /**
     * 账号已存在
     */
    USERNAME_EXIST(501,"账号已存在"),

    /**
     * 手机号已存在
     */
    PHONE_NUMBER_EXIST(502,"手机号已存在"),

    /**
     * 邮箱已存在
     */
    EMAIL_EXIST(503, "邮箱已存在"),

    /**
     * 必需填写用户名
     */
    REQUIRE_USERNAME(504, "必需填写用户名"),

    /**
     * 用户名或密码错误
     */
    LOGIN_ERROR(505,"用户名或密码错误"),

    /**
     * 评论内容不能为空
     */
    CONTENT_NOT_NULL(506,"评论内容不能为空"),

    /**
     * 文件类型错误
     */
    FILE_TYPE_ERROR(507,"文件类型错误，请上传png或者jpg文件"),

    /**
     * 文件内容不能为空
     */
    FILE_CONTENT_ERROR(508,"文件内容不能为空"),

    /**
     * 用户名不能为空
     */
    USERNAME_NOT_NULL(509, "用户名不能为空"),

    /**
     * 昵称不能为空
     */
    NICKNAME_NOT_NULL(510, "昵称不能为空"),

    /**
     * 密码不能为空
     */
    PASSWORD_NOT_NULL(511, "密码不能为空"),

    /**
     * 邮箱不能为空
     */
    EMAIL_NOT_NULL(512, "邮箱不能为空"),

    /**
     * 验证码不能为空
     */
    CAPTCHA_NOT_NULL(513, "验证码不能为空"),

    /**
     * 昵称已存在
     */
    NICKNAME_EXIST(514, "昵称已存在"),
    /**
     * 请不要频繁操作
     */
    LIMIT_ERROR(515, "请不要频繁操作");
    /** 状态码 */
    int code;
    /** 信息提示 */
    String msg;

    AppHttpCodeEnum(int code, String errorMessage){
        this.code = code;
        this.msg = errorMessage;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
