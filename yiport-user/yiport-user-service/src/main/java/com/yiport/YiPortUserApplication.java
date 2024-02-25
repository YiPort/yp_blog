package com.yiport;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan("com.yiport.mapper")
@EnableFeignClients
@EnableAsync
public class YiPortUserApplication {
    public static void main(String[] args) {

        SpringApplication.run(YiPortUserApplication.class, args);
    }
}