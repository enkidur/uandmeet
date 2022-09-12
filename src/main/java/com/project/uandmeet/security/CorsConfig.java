package com.project.uandmeet.security;

import com.project.uandmeet.security.jwt.JwtProperties;
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
        config.addAllowedOriginPattern("*");
        config.setAllowCredentials(true); // 내 서버가 응답할 때 json 을 자바스크립터에서 처리할 수 있을지에 대한 설정
        config.addAllowedHeader("*"); // 모든 header 에 응답을 허용
        config.addAllowedMethod("*"); // 모든 CRUD의 요청을 허용
        config.addExposedHeader(JwtProperties.HEADER_ACCESS); // 없으면 프론트측 Header에 나타나지않고 network에만 나타나게됨
        config.addExposedHeader("username");
        config.addExposedHeader("nickname");
        config.addExposedHeader("profile");
        config.addExposedHeader("loginto");
        config.addExposedHeader("Authorization");
        source.registerCorsConfiguration("/**",config); // resource 에 등록 url /api/** 는 모두 config 를 따름
        return new CorsFilter(source); // source 를 하나 만듦
    }

}
