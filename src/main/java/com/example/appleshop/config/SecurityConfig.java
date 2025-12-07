package com.example.appleshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
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
                        .requestMatchers(HttpMethod.GET, "/api/variants/product/{productId}", "/api/products/{productId}","/api/variants").permitAll()

                        .requestMatchers(
                                "/api/auth/login",
                                "/api/users",
                                "/api/users/register",
                                "/api/products",
                                "/api/categories",
                                "/api/variants/find",
                                "/api/variants/min-price/{productId}",
                                "/api/auth/logout",
                                "/api/auth/me",
                                "/html/dangnhap.html",
                                "/html/dangki.html",
                                "/html/TrangChu.html",
                                "/css/**",
                                "/js/**",
                                "/img/**",
                                "/api/reviews/product/{productId}",
                                "/html/index.html",
                                "/html/verify.html",
                                "/html/product-detail.html"

                        ).permitAll()
                        // Chỉ ADMIN hoặc STAFF được vào admin.html
                        .requestMatchers("/html/admin.html").hasAnyRole("ADMIN", "STAFF")
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
