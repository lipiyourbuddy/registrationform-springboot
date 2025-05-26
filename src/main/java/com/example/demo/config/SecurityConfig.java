package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/register", "/logout", "/login", "/send-otp", "/verify-otp", "/css/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin().disable()
            .logout().logoutSuccessUrl("/register"); // ✅ This sets your logout redirect

        return http.build();
    }
}

