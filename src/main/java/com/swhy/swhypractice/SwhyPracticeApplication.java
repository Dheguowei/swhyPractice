package com.swhy.swhypractice;

import com.github.jeffreyning.mybatisplus.conf.EnableMPP;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@SpringBootApplication
@MapperScan("com.swhy.swhypractice.mapper")
@EnableMPP
public class SwhyPracticeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SwhyPracticeApplication.class, args);
    }

}
