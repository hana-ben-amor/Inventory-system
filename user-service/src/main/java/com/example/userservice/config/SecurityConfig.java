package com.example.userservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig{
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        req->req.requestMatchers("/users/login","/users/validate-token","/users/register","/users/**").permitAll()
                                /*requestMatchers("/users/**","/admin/**","/super-admin/**","/admin/manage/**","/student/manage/**","/professor/manage/**","/admin/manage/**","/api/roles").hasRole("SUPER_ADMIN")
                                .requestMatchers("/student/schedule", "/student/notes", "/student/actuality", "/student/group").hasRole("STUDENT")
                                .requestMatchers("/professor/**").hasRole("PROFESSOR")
                                .requestMatchers("/admin/hr/**").hasRole("ADMIN_HR")
                                .requestMatchers("/admin/schedule/**").hasRole("ADMIN_SCHEDULE")
                                .requestMatchers("/admin/actualities/**").hasRole("ADMIN_ACTUALITIES")
                                .requestMatchers("/auth/**","/api/**").permitAll()*/
                )
                .sessionManagement(
                        session->session.sessionCreationPolicy(STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
        }
}
