package com.bttf.queosk.config;

import com.bttf.queosk.service.RefreshTokenService.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String token = getTokenFromRequest(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {

            Authentication authentication = jwtTokenProvider.getAuthentication(token);

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);
        } else if (token != null) {
            //만약 AccessToken이 유효하지 않을 경우 사용자의 이메일로 RefreshToken 조회
            String email = jwtTokenProvider.getEmailFromToken(token);
            String refreshToken = refreshTokenService.getRefreshToken(email);

            //리프레시 토큰이 유효할 경우 , 헤더에 리프레시 토큰 담기
            if (jwtTokenProvider.validateToken(refreshToken)) {
                String newAccessToken = refreshTokenService.issueNewAccessToken(email);
                response.setHeader(HttpHeaders.AUTHORIZATION, newAccessToken);
                response.setStatus(HttpServletResponse.SC_OK); // 200 Ok
                response.getWriter().write("토큰이 갱신되었습니다.");
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
                response.getWriter().write("토큰이 만료되었습니다. 다시 로그인 하세요.");
                response.getWriter().flush();
            }
        }
        chain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {

        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
