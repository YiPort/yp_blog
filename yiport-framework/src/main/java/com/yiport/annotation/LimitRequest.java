package com.yiport.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD) // 说明该注解只能放在方法上面
@Retention(RetentionPolicy.RUNTIME)
public @interface LimitRequest {
    /**
     * 限制时间 单位：毫秒
     */
    long time() default 5000;

    /**
     * 允许请求的次数
     */
    int count() default 1;

    /**
     * 返回描述
     */
    String description() default "接口请求超过次数";
}