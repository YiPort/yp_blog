package com.yiport.config;


import com.yiport.handler.exception.GlobalExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 网关配置
 *
 * @author y
 */
@Configuration(proxyBeanMethods = false)
public class GatewayConfig {

	@Bean
	public GlobalExceptionHandler globalExceptionHandler() {
		return new GlobalExceptionHandler();
	}

}