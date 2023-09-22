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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults()) // CORS 설정 허용
                .headers(c -> c.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable).disable())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        //사용자 접근만 허용
                        .antMatchers(
                                "/api/payment/*",                // 사용자 카카오페이
                                "/api/user/order",               // 사용자 주문 등록
                                "/api/restaurants/*/queue",      // 사용자 웨이팅 등록
                                "/api/restaurants/*/user/queue", // 사용자 웨이팅 취소
                                "/api/users"                    // 사용자 본인 정보관련
                        ).hasRole("USER")
                        //매장 접근만 허용
                        .antMatchers(
                                "/api/restaurants/menus/*",        // 메뉴 관리 api
                                "/api/restaurant/order/*",         // 매장 주문관리
                                "/api/restaurants/queue",          // 매장 큐 관리
                                "/api/restaurants/image",          // 매장 이미지 추가
                                "/api/restaurants",                // 매장 정보 관리
                                "/api/restaurants/settlement/*",   // 매장 정산 관리
                                "/api/restaurant/table"            // 매장 테이블 관련
                        ).hasRole("RESTAURANT")
                        //검증 미실시
                        .antMatchers(
                                "/**/signup",                     // 회원가입 관련
                                "/**/signin",                     // 로그인 관련
                                "/**/verification",               // 이메일검증 관련
                                "/**/refresh",                    // 토큰갱신 관련
                                "/**/callback",                   // 기타 콜백
                                "/**/users/check",                // 가입전 이메일 중복확인
                                "/password/reset",                // 비로그인 비밀번호 리셋
                                "/api/restaurants/coord",         // 매장 조회 (좌표)
                                "/api/restaurants/keyword",         // 매장 조회 (키워드)
                                "/api/restaurants/*/menus",       // 매장 메뉴 조회
                                "/api/restaurants/*/details",     // 매장 상세조회
                                "/swagger-ui/**",                 // 스웨거 관련
                                "/v2/api-docs",
                                "/swagger-resources/**",
                                "/webjars/**",                     // Webjar 관련
                                "/api/autocomplete"               // 매장검색어 자동완성
                        ).permitAll()
                        //외 모든 경로 검증 실시
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
