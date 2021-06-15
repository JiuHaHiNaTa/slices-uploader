package com.example.upload.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web相关配置
 *
 * @author Jiuha
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //对那些请求路径进行跨域处理
        registry.addMapping("/**")
                //允许的请求头，默认允许所有的请求头
                .allowedHeaders("*")
                //允许的方法，默认允许GET、POST、HEAD
                .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE")
                //探测请求有效时间，单位秒
                .maxAge(3600)
                //支持的域
                .allowedOrigins("*");
    }
}
