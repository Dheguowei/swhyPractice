package com.swhy.swhypractice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .pathMapping("/")
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("API接口文档")//标题
                .description("申万宏源实习监控系统")//描述
                .termsOfServiceUrl("gwDemo")//服务的团队
                .contact(new Contact("qt", "blog.csdn.net", "xxxxxxxx@163.com"))//作者的信息
                .license("The Apache License")//授权信息
                .licenseUrl("http://www.baidu.com")//授权的url
                .version("1.0.0")//版本号
                .build();
    }
}
