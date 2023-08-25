package com.bttf.queosk.config.springsecurity;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    private static final String[] ALL_WHITELIST = {
            "/**/signup",                // 로그인전이므로 AccessToken 체크 패스
            "/**/signin",                // 로그인전이므로 AccessToken 체크 패스
            "/**/verification",          // 로그인전이므로 AccessToken 체크 패스
            "/**/refresh",		         // 토큰갱신이므로 AccessToken 체크 패스
            "/**/callback"               // 외부 api 콜백이므로 AccessToken 체크 패스
    };

    //화이트리스트(필터링 대상인지 아닌지 판별)
    private boolean isFilterCheck(String requestURI){
        return !PatternMatchUtils.simpleMatch(ALL_WHITELIST, requestURI);
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain chain) throws IOException {

        String token = getTokenFromRequest(request);

        try {
            // 화이트리스트에 있는 경우에는 필터링을 건너뛰어서 다음 필터로 진행
            if (isFilterCheck(request.getRequestURI())) {
                // 화이트리스트에 없는 경우에만 검증 처리
                if (token != null) {
                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(jwtTokenProvider.getAuthentication(token));
                }
            }
            chain.doFilter(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }
    }
    private String getTokenFromRequest(HttpServletRequest request) {

        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
