package com.bttf.queosk.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults()) // CORS 설정 허용
                .headers(c -> c.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable).disable())
                .authorizeHttpRequests(auth -> auth
                        .antMatchers(
                                "/api/payment/*",                // 사용자 카카오페이
                                "/api/user/order",               // 사용자 주문 등록
                                "/api/restaurants/*/queue",      // 사용자 웨이팅 등록
                                "/api/restaurants/*/user/queue", // 사용자 웨이팅 취소
                                "/api/users",                    // 사용자 본인 정보관련
                                "/api/users/password/*"          // 사용자 비밀번호 관련
                        ).hasRole("USER")
                        .antMatchers(
                                "/api/restaurants/menus/*",        // 메뉴 관리 api
                                "/api/restaurant/order/*",         // 매장 주문관리
                                "/api/restaurants/queue",          // 매장 큐 관리
                                "/api/restaurants/password/*",     // 매장 비밀번호 변경
                                "/api/restaurants/image",          // 매장 이미지 추가
                                "/api/restaurants",                // 매장 정보 관리
                                "/api/restaurants/settlement/*",   // 매장 정산 관리
                                "/api/restaurant/table"            // 매장 테이블 관련
                        ).hasRole("RESTAURANT")
                        .anyRequest()
                        .authenticated()
                );

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
