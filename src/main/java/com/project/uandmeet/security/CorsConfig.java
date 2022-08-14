package com.project.uandmeet.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    // Filter 에 등록해야 됨(여기서는 SecurityConfig)
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // 내 서버가 응답을할 때 json 을 자바스크립터에서 처리할 수 있지에 대한 설정
//        config.addAllowedOrigin("*"); // 모든 ip 에 응답을 허용
        config.addAllowedOriginPattern("*"); // 모든 ip 에 응답을 허용
        config.addAllowedHeader("*"); // 모든 header 에 응답을 허용
        config.addAllowedMethod("*"); // 모든 CRUD의 요청을 허용
        source.registerCorsConfiguration("/api/**",config); // resource 에 등록 url /api/** 는 모두 config 를 따름
        return new CorsFilter(source); // source 를 하나 만듦
    }

}
