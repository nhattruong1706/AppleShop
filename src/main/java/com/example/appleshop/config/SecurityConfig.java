package com.example.appleshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/users",
                                "/api/products",
                                "/api/auth/logout",
                                "/api/auth/me",
                                "/html/dangnhap.html",
                                "/html/dangki.html",
                                "/html/TrangChu.html",
                                "/css/**",
                                "/js/**",
                                "/img/**",
                                "/html/index.html",
                                "/html/verify.html"

                        ).permitAll()
                        // 🔒 Chặn tất cả các file HTML còn lại
                        .requestMatchers("/html/**").authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/html/dangnhap.html")  // trang login custom
                        .permitAll()
                );

        return http.build();
    }




}
