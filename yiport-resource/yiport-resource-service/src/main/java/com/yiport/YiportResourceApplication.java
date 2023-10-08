package com.yiport;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.yiport.mapper")
@EnableFeignClients
public class YiportResourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(YiportResourceApplication.class, args);
    }

}
