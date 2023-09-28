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
    public void createUser(UserSignUpRequest userSignUpRequest) {

        //기존회원 여부 확인 (소문자 치환 후 조회)
        userRepository.findByEmail(userSignUpRequest.getEmail().toLowerCase())
                .ifPresent(user -> {
                    throw new CustomException(EXISTING_USER);
                });

        //비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(userSignUpRequest.getPassword());

        //회원 휴대폰 번호 혹시라도 문자나 공백이 들어왔다면 처리
        String trimmedPhoneNumber =
                userSignUpRequest.getPhone().replaceAll("\\D", "");

        User user = User.of(userSignUpRequest, encryptedPassword, trimmedPhoneNumber);

        userRepository.save(user);

        //회원 이메일 주소로 인증이메일 전송
        emailSender.sendEmail(userSignUpRequest.getEmail(),
                "Queosk 이메일 인증",
                String.format("Queosk에 가입해 주셔서 감사합니다. \n" +
                        "아래 링크를 클릭 하시어 이메일 인증을 완료해주세요.\n" +
                        "https://queosk.kr/api/users/%d/verification", user.getId()));
    }

    @Transactional
    public UserSignInDto signInUser(UserSignInRequest userSignInRequest) {

        //입력된 email 로 사용자 조회
        User user = userRepository.findByEmail(userSignInRequest.getEmail())
                .orElseThrow(() -> new CustomException(INVALID_USER_ID));

        //조회된 사용자의 비밀번호와 입력된 비밀번호 비교
        if (!passwordEncoder.matches(userSignInRequest.getPassword(), user.getPassword())) {
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