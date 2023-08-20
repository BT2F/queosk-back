package com.bttf.queosk.service.refreshtokenservice;

import com.bttf.queosk.config.springsecurity.JwtTokenProvider;
import com.bttf.queosk.dto.tokendto.NewAccessTokenDto;
import com.bttf.queosk.dto.tokendto.TokenDto;
import com.bttf.queosk.entity.RefreshToken;
import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.entity.User;
import com.bttf.queosk.exception.CustomException;
import com.bttf.queosk.repository.RefreshTokenRepository;
import com.bttf.queosk.repository.RestaurantRepository;
import com.bttf.queosk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.bttf.queosk.exception.ErrorCode.*;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // 신규 AccessToken 발급 (User 토큰 재발급 시)
    public NewAccessTokenDto issueNewAccessTokenForUser(String refreshToken) {

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new CustomException(INVALID_TOKEN);
        }

        RefreshToken token = refreshTokenRepository.findById(refreshToken)
                .orElseThrow(() -> new CustomException(INVALID_TOKEN));

        User user = userRepository.findByEmail(token.getEmail())
                .orElseThrow(() -> new CustomException(USER_NOT_EXISTS));

        String newAccessToken = jwtTokenProvider.generateAccessToken(
                TokenDto.builder()
                        .id(user.getId())
                        .userRole(user.getUserRole())
                        .email(user.getEmail())
                        .build()
        );
        return NewAccessTokenDto.builder().accessToken(newAccessToken).build();
    }

    public NewAccessTokenDto issueNewAccessTokenForRestaurant(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new CustomException(INVALID_TOKEN);
        }

        RefreshToken token = refreshTokenRepository.findById(refreshToken)
                .orElseThrow(() -> new CustomException(INVALID_TOKEN));

        Restaurant restaurant = restaurantRepository.findByEmail(token.getEmail())
                .orElseThrow(()-> new CustomException(INVALID_RESTAURANT));

        String newAccessToken = jwtTokenProvider.generateAccessToken(
                TokenDto.builder()
                        .id(restaurant.getId())
                        .userRole(restaurant.getUserRole())
                        .email(restaurant.getEmail())
                        .build()
        );
        return NewAccessTokenDto.builder().accessToken(newAccessToken).build();
    }

    public void deleteRefreshToken(String email) {
        refreshTokenRepository.deleteById(email);
    }
}
