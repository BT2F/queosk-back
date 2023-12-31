package com.bttf.queosk.service;

import com.bttf.queosk.config.JwtTokenProvider;
import com.bttf.queosk.dto.RefreshTokenDto;
import com.bttf.queosk.dto.TokenDto;
import com.bttf.queosk.dto.TokenRefreshDto;
import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.entity.User;
import com.bttf.queosk.enumerate.UserRole;
import com.bttf.queosk.exception.CustomException;
import com.bttf.queosk.repository.RefreshTokenRepository;
import com.bttf.queosk.repository.RestaurantRepository;
import com.bttf.queosk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.bttf.queosk.enumerate.UserRole.ROLE_USER;
import static com.bttf.queosk.exception.ErrorCode.*;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // 신규 AccessToken 발급
    @Transactional
    public TokenRefreshDto issueNewAccessToken(String accessToken, String refreshToken) {
        validateRefreshToken(refreshToken);

        String emailFromToken = jwtTokenProvider.getEmailFromToken(refreshToken);

        RefreshTokenDto tokenSubject = refreshTokenRepository.findByEmail(emailFromToken);

        if (tokenSubject == null) {
            throw new CustomException(REFRESH_CODE_EXPIRED);
        }

        Object tokenHolder = getObject(accessToken, tokenSubject);

        Long id = null;
        UserRole userRole = null;
        String email = null;

        if (tokenHolder instanceof User) {
            User user = (User) tokenHolder;
            id = user.getId();
            userRole = user.getUserRole();
            email = user.getEmail();
        } else if (tokenHolder instanceof Restaurant) {
            Restaurant restaurant = (Restaurant) tokenHolder;
            id = restaurant.getId();
            userRole = restaurant.getUserRole();
            email = restaurant.getEmail();
        }

        String newAccessToken = jwtTokenProvider.generateAccessToken(
                TokenDto.of(id, userRole, email)
        );
        return TokenRefreshDto.of(newAccessToken);
    }

    // 리프레시 토큰 삭제
    @Transactional
    public void deleteRefreshToken(String email) {
        refreshTokenRepository.deleteByEmail(email);
    }

    // RefreshTokenDto 유효성 검사
    private void validateRefreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new CustomException(INVALID_TOKEN);
        }
    }

    // AccessToken을 이용하여 User 또는 Restaurant 객체 가져오기
    private Object getObject(String accessToken, RefreshTokenDto tokenSubject) {
        return jwtTokenProvider.getRoleFromToken(accessToken).equals(
                ROLE_USER.toString()) ?
                findUser(tokenSubject.getEmail()) :
                findRestaurant(tokenSubject.getEmail()
                );
    }

    // User 객체 찾기
    private User findUser(String userEmail) {
        return userRepository.findByEmail(userEmail.replace("queosk_auth: ",""))
                .orElseThrow(() -> new CustomException(USER_NOT_EXISTS));
    }

    // Restaurant 객체 찾기
    private Restaurant findRestaurant(String restaurantEmail) {
        return restaurantRepository.findByEmail(restaurantEmail)
                .orElseThrow(() -> new CustomException(INVALID_RESTAURANT));
    }
}
