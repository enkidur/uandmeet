package com.project.uandmeet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.TimeZone;

@EnableJpaAuditing
@SpringBootApplication
public class UandmeetApplication {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
//    @Bean
//    public JavaMailSenderImpl mailSender() { return new JavaMailSenderImpl(); }

    @PostConstruct
    public  void started(){
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));

        System.out.println("현재시각:"+ new Date());
    }

    public static void main(String[] args) {
        SpringApplication.run(UandmeetApplication.class, args);
    }
}
