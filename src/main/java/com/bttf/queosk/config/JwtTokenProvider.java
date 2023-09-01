package com.bttf.queosk.config;

import com.bttf.queosk.dto.TokenDto;
import com.bttf.queosk.enumerate.UserRole;
import com.bttf.queosk.service.UserTokenDetailService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.bttf.queosk.enumerate.UserRole.ROLE_USER;
import static com.bttf.queosk.enumerate.UserRole.valueOf;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    private final UserTokenDetailService userDetailsService;

    @Value("${jwt.tokenIssuer}")
    private String issuer;
    private SecretKey key;

    @PostConstruct
    public void init() {
        key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    public String generateAccessToken(TokenDto tokenDto) {
        Claims claims = Jwts.claims().setSubject(tokenDto.getId().toString());
        claims.put("email", tokenDto.getEmail());
        claims.put("userRole", tokenDto.getUserRole().getRoleName()); // 역할 정보 추가

        return Jwts.builder()
                .setClaims(claims)
                .setIssuer(issuer)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                // 유효기간 1일 (24시간)
                .setExpiration(new Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000)))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken() {
        return Jwts.builder()
                .claim("userRole", ROLE_USER.getRoleName())
                .setIssuer(issuer)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                //유효기간 15일 (24시간 *15 )
                .setExpiration(new Date(System.currentTimeMillis() + (15 * 24 * 60 * 60 * 1000)))
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException ex) {
            return false;
        }
    }

    public Long getIdFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return Long.parseLong(Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject());
    }

    public String getEmailFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("email", String.class);
    }

    public Authentication getAuthentication(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String email = claims.get("email", String.class);
        UserRole userRole = valueOf(claims.get("userRole", String.class));

        // ROLE에 따라 다른 메서드 호출
        UserDetails userDetails =
                userRole.getRoleName().equals(ROLE_USER.toString()) ?
                        userDetailsService.loadUserByUsername(email) :
                        userDetailsService.loadRestaurantByUsername(email);

        List<GrantedAuthority> authorities = new ArrayList<>(userDetails.getAuthorities());
        authorities.addAll(userRole.getAuthorities()); // 추가된 역할 권한

        return new UsernamePasswordAuthenticationToken(
                userDetails, "", authorities
        );
    }

    public String getRoleFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return valueOf(claims.get("userRole", String.class)).getRoleName();
    }
}
