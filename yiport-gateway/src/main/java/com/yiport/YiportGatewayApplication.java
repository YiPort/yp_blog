package com.yiport.gateway;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.yiport.mapper")
@EnableFeignClients
public class YiportGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(YiportGatewayApplication.class, args);
    }

}
