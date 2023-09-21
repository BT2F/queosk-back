package com.bttf.queosk.service;

import com.bttf.queosk.dto.UserDto;
import com.bttf.queosk.dto.UserEditRequest;
import com.bttf.queosk.dto.UserPasswordChangeRequest;
import com.bttf.queosk.entity.User;
import com.bttf.queosk.enumerate.LoginType;
import com.bttf.queosk.exception.CustomException;
import com.bttf.queosk.repository.KakaoAuthRepository;
import com.bttf.queosk.repository.RefreshTokenRepository;
import com.bttf.queosk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.bttf.queosk.enumerate.UserStatus.DELETED;
import static com.bttf.queosk.enumerate.UserStatus.VERIFIED;
import static com.bttf.queosk.exception.ErrorCode.*;

@RequiredArgsConstructor
@Service
public class UserInfoService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailSender emailSender;
    private final RefreshTokenRepository refreshTokenRepository;
    private final KakaoAuthRepository kakaoAuthRepository;

    @Transactional
    public UserDto editUserInformation(Long userId, UserEditRequest userEditRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_EXISTS));

        user.setNickName(userEditRequest.getNickName());
        user.setPhone(userEditRequest.getPhone());

        userRepository.save(user);

        return UserDto.of(user);
    }

    @Transactional
    public void changeUserPassword(Long userId,
                                   UserPasswordChangeRequest userPasswordChangeRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_EXISTS));

        if (user.getLoginType().equals(LoginType.KAKAO)) {
            throw new CustomException(KAKAO_USER_UNSUPPORTED_SERVICE);
        }

        if (!passwordEncoder.matches(userPasswordChangeRequest.getExistingPassword(), user.getPassword())) {
            throw new CustomException(PASSWORD_NOT_MATCH);
        }

        user.setPassword(passwordEncoder.encode(userPasswordChangeRequest.getNewPassword()));

        userRepository.save(user);
    }

    @Transactional
    public void resetUserPassword(String email, String nickName) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(USER_NOT_EXISTS));

        if (user.getLoginType().equals(LoginType.KAKAO)) {
            throw new CustomException(KAKAO_USER_UNSUPPORTED_SERVICE);
        }

        if (!user.getNickName().equals(nickName)) {
            throw new CustomException(NICKNAME_NOT_MATCH);
        }

        String randomPassword =
                UUID.randomUUID().toString().replace("-", "").substring(0, 10);

        String encryptedPassword = passwordEncoder.encode(randomPassword);

        emailSender.sendEmail(
                user.getEmail(),
                "비밀번호 초기화 완료",
                "새로운 비밀번호 : " + randomPassword + "를 입력해 로그인 해주세요."
        );

        user.setPassword(encryptedPassword);

        userRepository.save(user);
    }

    @Transactional
    public void updateImageUrl(Long id, String url) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(USER_NOT_EXISTS));

        user.setImageUrl(url);

        userRepository.save(user);
    }

    @Transactional
    public void withdrawUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(USER_NOT_EXISTS));

        refreshTokenRepository.deleteByEmail(user.getEmail());

        if (user.getLoginType().equals(LoginType.KAKAO)) {
            kakaoAuthRepository.deleteByEmail(user.getEmail());
        }

        user.setUserStatus(DELETED);

        userRepository.save(user);
    }

    @Transactional
    public String verifyUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_EXISTS));

        if (user.getStatus().equals(DELETED)) {
            return "탈퇴한 회원입니다.";
        } else if (user.getStatus().equals(VERIFIED)) {
            return "이미 인증이 완료된 회원입니다.";
        }

        user.setUserStatus(VERIFIED);

        userRepository.save(user);

        return "인증이 완료되었습니다.";
    }

    public UserDto getUserDetails(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_EXISTS));

        return UserDto.of(user);
    }
}
