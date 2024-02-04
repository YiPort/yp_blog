package com.yiport.annotation;

import java.lang.annotation.*;

import static com.yiport.constants.SystemConstants.FALSE;

/**
 * 限流注解
 */
@Documented
@Target(ElementType.METHOD) // 说明该注解只能放在方法上面
@Retention(RetentionPolicy.RUNTIME)
public @interface LimitRequest
{
    /**
     * 限制时间 单位：毫秒
     */
    long time() default 5000;

    /**
     * 允许请求的次数
     */
    int count() default 1;

    /**
     * 限流类型
     * <p>根据ip-IP
     * <p>根据用户-USER
     */
    String type() default "IP";

    /**
     * 是否对管理员限制
     */
    boolean limitAdmin() default true;

    /**
     * 默认返回描述
     */
    String description() default "请不要频繁操作";

    /**
     * 前端提示类型
     * <p>不提示-FALSE
     * <p>精确提示（精确到秒）-TRUE
     * <p>自定义-DEFINED
     */
    String tip() default FALSE;
}