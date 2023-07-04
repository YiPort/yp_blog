package cn.yiport;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @Author YiPort
 */
@SpringBootApplication
@MapperScan("cn.yiport.mapper")
@EnableScheduling
@EnableSwagger2
public class YiPortBlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(YiPortBlogApplication.class,args);
    }
}