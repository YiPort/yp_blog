package com.yiport;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Author YiPort
 */
@SpringBootApplication
@MapperScan("com.yiport.mapper")
@EnableScheduling
public class YiPortBlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(YiPortBlogApplication.class,args);
    }
}