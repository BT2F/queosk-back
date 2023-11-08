package com.bttf.queosk.service;

import com.bttf.queosk.config.JwtTokenProvider;
import com.bttf.queosk.dto.*;
import com.bttf.queosk.entity.User;
import com.bttf.queosk.exception.CustomException;
import com.bttf.queosk.repository.RefreshTokenRepository;
import com.bttf.queosk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.bttf.queosk.enumerate.EmailComponents.VERIFICATION_SUBJECT;
import static com.bttf.queosk.enumerate.EmailComponents.VERIFICATION_TEXT;
import static com.bttf.queosk.enumerate.UserStatus.DELETED;
import static com.bttf.queosk.enumerate.UserStatus.NOT_VERIFIED;
import static com.bttf.queosk.exception.ErrorCode.*;

@RequiredArgsConstructor
@Service
public class UserLoginService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailSender emailSender;

    @Transactional
    public void createUser(UserSignUpRequestForm userSignUpRequestForm) {

        //기존회원 여부 확인 (소문자 치환 후 조회)
        userRepository.findByEmail(userSignUpRequestForm.getEmail().toLowerCase())
                .ifPresent(user -> {
                    throw new CustomException(EXISTING_USER);
                });

        //비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(userSignUpRequestForm.getPassword());

        //회원 휴대폰 번호 혹시라도 문자나 공백이 들어왔다면 처리
        String trimmedPhoneNumber =
                userSignUpRequestForm.getPhone().replaceAll("\\D", "");

        User user = User.of(userSignUpRequestForm, encryptedPassword, trimmedPhoneNumber);

        userRepository.save(user);

        //회원 이메일 주소로 인증이메일 전송
        emailSender.sendEmail(userSignUpRequestForm.getEmail(),
                VERIFICATION_SUBJECT,
                String.format(VERIFICATION_TEXT, user.getId()));
    }

    @Transactional
    public UserSignInDto signInUser(UserSignInRequestForm userSignInRequestForm) {

        //입력된 email 로 사용자 조회
        User user = userRepository.findByEmail(userSignInRequestForm.getEmail())
                .orElseThrow(() -> new CustomException(INVALID_USER_ID));

        //조회된 사용자의 비밀번호와 입력된 비밀번호 비교
        if (!passwordEncoder.matches(userSignInRequestForm.getPassword(), user.getPassword())) {
            throw new CustomException(PASSWORD_NOT_MATCH);
        }

        //탈퇴한 사용자의 경우
        if (user.getStatus().equals(DELETED)) {
            throw new CustomException(WITHDRAWN_USER);
        } else if (user.getStatus().equals(NOT_VERIFIED)) {
            throw new CustomException(NOT_VERIFIED_USER);
        }

        //엑세스 토큰 발행
        String accessToken = jwtTokenProvider.generateAccessToken(TokenDto.of(user));

        //리프레시 토큰 발행
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        //리프테시 토큰 저장(기존에 있었다면 덮어쓰기 - 성능상 조회 후 수정보다 덮어쓰기가 더 빠르고 가벼움)
        refreshTokenRepository.save(user.getEmail(), refreshToken);

        return UserSignInDto.of(user, refreshToken, accessToken);
    }

    public boolean checkDuplication(String email) {
        return !userRepository.findByEmail(email.toLowerCase()).isPresent();
    }

    @Transactional(readOnly = true)
    public UserDto getUserFromToken(String token) {
        Long userId = jwtTokenProvider.getIdFromToken(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_EXISTS));

        return UserDto.of(user);
    }

    @Transactional(readOnly = true)
    public UserDto getUserFromId(Long id) {
        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(USER_NOT_EXISTS));

        return UserDto.of(targetUser);
    }
}