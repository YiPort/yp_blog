package com.yiport.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Security 配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "security")
public class SecurityProperties
{

    /**
     * 排除路径
     */
    private String[] excludes;

    /**
     * 拦截路径
     */
    private String[] Intercepts;

}
