package com.example.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class CorsConfiguration {
    public static final String GET="GET";
    public static final String PUT="PUT";
    public static final String POST="POST";
    public static final String DELETE="DELETE";

    @Bean
    public WebMvcConfigurer corsConfigurer(){
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedMethods(GET,PUT,POST,DELETE)
                        .allowedHeaders("*")
                        .allowedOriginPatterns("*")
                        .allowCredentials(true)
                        ;
            }};

    }
}
