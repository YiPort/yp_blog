package cn.yiport;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author YiPort
 */
@SpringBootApplication
@MapperScan("cn.yiport.mapper")
public class YiPortBlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(YiPortBlogApplication.class,args);
    }
}