package com.edulanguage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Điểm vào ứng dụng Spring Boot.
 * Kết nối SQL Server qua cấu hình trong application.properties.
 */
@SpringBootApplication
public class EduLanguageApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(EduLanguageApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(EduLanguageApplication.class, args);
    }
}
