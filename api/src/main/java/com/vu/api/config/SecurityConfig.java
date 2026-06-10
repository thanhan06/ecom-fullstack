package com.vu.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Cần đăng nhập để đăng xuất
                        .requestMatchers(HttpMethod.POST, "/auth/logout")
                        .authenticated()

                        // 1. CÁC API PUBLIC (Không cần token)
                        // Cho phép tất cả các API liên quan đến xác thực (Login, Register...)
                        .requestMatchers("/auth/**", "/api/v1/auth/**")
                        .permitAll()
                        // Mở cửa API tạo refresh token
                        .requestMatchers("/refresh-token/**")
                        .permitAll()

                        // 2. CÁC API LIÊN QUAN ĐẾN USER
                        // Mở cửa cho hành động tạo User mới (Đăng ký)
                        .requestMatchers(HttpMethod.POST, "/users")
                        .permitAll()
                        // Cần đăng nhập để xem thông tin cá nhân
                        .requestMatchers("/users/me")
                        .authenticated()

                        // Chỉ Admin mới được xem danh sách tất cả users
                        // Chỉ Admin hoặc chủ sở hữu mới được xem thông tin chi tiết (sẽ phân quyền kỹ hơn ở Controller)
                        .requestMatchers("/users/**")
                        .authenticated()

                        // 3. TẤT CẢ CÁC REQUEST CÒN LẠI ĐỀU PHẢI ĐĂNG NHẬP
                        .anyRequest()
                        .authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
