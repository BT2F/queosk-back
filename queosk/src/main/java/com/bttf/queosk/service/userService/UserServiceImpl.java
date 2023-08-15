package com.bttf.queosk.service.userService;

import com.bttf.queosk.config.JwtTokenProvider;
import com.bttf.queosk.dto.userDto.UserSignUpForm;
import com.bttf.queosk.entity.User;
import com.bttf.queosk.exception.CustomException;
import com.bttf.queosk.repository.RefreshTokenRepository;
import com.bttf.queosk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static com.bttf.queosk.exception.ErrorCode.EXISTING_USER;
import static com.bttf.queosk.model.UserStatus.NOT_VERIFIED;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void createUser(UserSignUpForm userSignUpForm) {

        //기존회원 여부 확인
        userRepository.findByEmail(userSignUpForm.getEmail()).ifPresent(
                user -> {
                    throw new CustomException(EXISTING_USER);
                });

        //비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(userSignUpForm.getPassword());

        //회원 휴대폰 번호 혹시라도 문자나 공백이 들어왔다면 처리
        String trimmedPhoneNumber =
                userSignUpForm.getPhone().replaceAll("\\D", "");

        userRepository.save(
                User.builder()
                        .email(userSignUpForm.getEmail())
                        .nickName(userSignUpForm.getNickName())
                        .password(encryptedPassword)
                        .phone(trimmedPhoneNumber)
                        .status(NOT_VERIFIED)
                        .build()
        );
    }
}
