package com.yiport.constent;

/**
 * 用户常量
 */
public interface UserConstant {
    /**
     * 用户登录成功
     */
    String LOGIN_SUCCESS = "登录成功";

    /**
     * 用户登录失败
     */
    String LOGIN_FAIL = "登录失败";

    /**
     * 用户注册成功
     */
    String REGISTER_SUCCESS = "注册成功";

    /**
     * 用户注册失败
     */
    String REGISTER_FAIL = "注册失败";

    /**
     * 成功
     */
    String SUCCESS = "0";

    /**
     * 失败
     */
    String FAIL = "1";

    // ----- 权限 -----

    /**
     * 默认权限
     */
    String DEFAULT_ROLE = "0";

    /**
     * 管理员权限
     */
    String ADMIN_ROLE = "1";

    // ------ 用户状态 ------

    /**
     * 状态正常
     */
    String NORMAL_STATUS = "0";

    /**
     * 封禁
     */
    String BAN_STATUS = "1";

    /**
     * 验证码-结果分隔符
     */
    String CAPTCHA_RESULT_SPLIT = "@";

    /**
     * 未知地址
     */
    String UNKNOWN = "XX XX";

    /**
     * 内网IP
     */
    String INTRANET_IP = "内网IP";

    /**
     * 国内访问IP对应地址统计
     */
    String IP_ADDRESS = "ipAddress";

    /**
     * 国外访问IP对应地址统计
     */
    String FOREIGN_IP = "foreignIp";

    /**
     * 所有访问IP
     */
    String IP = "ip";

    /**
     * Content-Type
     */
    String JPG = "image/jpg";
    String JPEG = "image/jpeg";
    String PNG = "image/png";

    /**
     * adminID
     */
    Long ADMIN_ID_1 = 1L;

    /**
     * adminID
     */
    Long ADMIN_ID_2 = 2L;

    /**
     * token-header-key
     */
    String TOKEN_HEADER_KEY = "token";

    /**
     * Token过期时间（3天）
     */
    Long EXPIRATION = 36 * 60 * 60 * 1000L;

    /**
     * 强制重新登录时间（10天）
     */
    Long LIMIT_TIME = 10 * 24 * 60 * 60 * 1000L;

    /**
     * Token刷新时间（12小时）
     */
    Long RELOAD_TIME = 12 * 60 * 60 * 1000L;

    /**
     * 图片上传限制大小（MB）
     */
    Integer IMAGE_SIZE = 1024 * 1024;

    /**
     * userInfo
     */
    String USER_INFO = "userInfo";

    /**
     * 邮箱校验
     */
    String EMAIL_REGEX = "[a-zA-Z0-9_]+@[a-zA-Z0-9_]+(\\.[a-zA-Z0-9]+)+";

    /**
     * 空字符校验
     */
    String NULL_REGEX = "[\\s]";

    /**
     * 特殊字符校验
     */
    String SPECIAL_REGEX = "[\\s`!@#$%^&*_\\-~()+=|{}':;,\\[\\].<>/\\\\?！￥…（）—【】‘；：”“’。，、？]";

    /**
     * 账号校验（只含有汉字、数字、字母、下划线并且长度在4-9之间）
     */
    String ACCOUNT_REGEX = "^[a-zA-Z0-9_\\u4e00-\\u9fa5]{4,9}$";

    /**
     * 密码校验（必须包含数字和英文字母和特殊字符（!@#$%^&*）并且长度在8-16之间）
     */
    String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$\\%\\^\\&\\*])[0-9a-zA-Z!@#$\\%\\^\\&\\*]{8,16}";

    /**
     * 系统自动分配昵称前缀
     */
    String NICKNAME_PREFIX = "用户";

    /**
     * UID区间标识（common）
     */
    String SECTION_MARK = "common";

    /**
     * verify-mail-subject
     */
    String VALIDATION_MESSAGE = "一点博客 | 验证消息";

    String CAPTCHA_CODES = "ypblog:user:captcha_codes:";

    /**
     * account-login-subject
     */
    String ACCOUNT_LOGIN_MESSAGE = "一点博客 | 账号登录告警";

    /**
     * login-byMail-subject
     */
    String LOGIN_BY_MAIL_MESSAGE = "一点博客 | 账号登录验证";

    /**
     * verify-mail-captcha-key
     */
    String VERIFY_MAIL_CAPTCHA = "verify_mail_captcha:";

    /**
     * 邮箱验证码过期时间（分钟）
     */
    Integer MAIL_CAPTCHA_TIME = 5;

    /**
     * 邮箱验证码长度
     */
    int MAIL_CAPTCHA_LENGTH = 6;

    /**
     * get_account_mail_captcha-key
     */
    String GET_ACCOUNT_MAIL_CAPTCHA = "get_account_mail_captcha:";

    /**
     * update-password-mail-captcha-key
     */
    String UPDATE_PASSWORD_MAIL_CAPTCHA = "update_password_mail_captcha:";

    /**
     * login-byMail-captcha-key
     */
    String LOGIN_BY_MAIL = "login_by_mail_captcha:";

    /**
     * X-Forwarded-For
     */
    String X_FORWARDED_FOR = "X-Forwarded-For";

    /**
     * 前端提示类型
     * <p>不提示-false
     * <p>精确提示（精确到秒）-true
     * <p>自定义-defined
     */
    String FALSE = "false";
    String TRUE = "true";
    String DEFINED = "defined";

    /**
     * 登录/访问类型-账号密码
     */
    String LOGIN_BY_ACCOUNT = "0";

    /**
     * 登录/访问类型-邮箱验证码
     */
    String LOGIN_BY_EMAIL = "1";

}
