package com.yiport;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.yiport.mapper")
@EnableFeignClients
public class YiPortSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(YiPortSearchApplication.class,args);
    }

}
