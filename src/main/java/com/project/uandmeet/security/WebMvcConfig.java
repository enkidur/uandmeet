package com.project.uandmeet.security;

import org.springframework.boot.web.servlet.view.MustacheViewResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // ioc 로 등록
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        MustacheViewResolver resolver = new MustacheViewResolver(); // mustache 재설정
        resolver.setCharset("UTF-8"); // 인코딩은 UTF-8
        resolver.setContentType("text/html; charset=UTF-8"); // 데이터는 html 이고 UTF-8형식
        resolver.setPrefix("classpath:/templates"); // 위치 templates
        resolver.setSuffix(".html"); // mustache 가 .html을 인식

        registry.viewResolver(resolver);
    }
}
