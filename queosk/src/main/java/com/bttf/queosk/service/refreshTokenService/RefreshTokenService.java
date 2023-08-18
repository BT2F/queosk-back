package com.bttf.queosk.service.refreshTokenService;

import com.bttf.queosk.config.springSecurity.JwtTokenProvider;
import com.bttf.queosk.dto.tokenDto.NewAccessTokenDto;
import com.bttf.queosk.dto.tokenDto.TokenDto;
import com.bttf.queosk.entity.RefreshToken;
import com.bttf.queosk.entity.User;
import com.bttf.queosk.exception.CustomException;
import com.bttf.queosk.repository.RefreshTokenRepository;
import com.bttf.queosk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.bttf.queosk.exception.ErrorCode.INVALID_TOKEN;
import static com.bttf.queosk.exception.ErrorCode.USER_NOT_EXISTS;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // 신규 AccessToken 발급
    public NewAccessTokenDto issueNewAccessToken(String refreshToken) {

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

    public void deleteRefreshToken(String email) {
        refreshTokenRepository.deleteById(email);
    }
}
