package com.yiport.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * springSecurity配置类
 *
 * @author ultima
 * @version 2022/9/27 15:56
 */
@Data
@Component
@ConfigurationProperties(prefix = "spring-security")
public class MySecurityProperties
{
    /**
     * 公开的资源
     */
    private String[] matchers;
}
