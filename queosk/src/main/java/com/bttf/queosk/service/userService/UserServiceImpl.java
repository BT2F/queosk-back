package com.bttf.queosk.service.userService;

import com.bttf.queosk.config.springSecurity.JwtTokenProvider;
import com.bttf.queosk.dto.userDto.*;
import com.bttf.queosk.entity.RefreshToken;
import com.bttf.queosk.entity.User;
import com.bttf.queosk.exception.CustomException;
import com.bttf.queosk.exception.ErrorCode;
import com.bttf.queosk.mapper.userMapper.TokenDtoMapper;
import com.bttf.queosk.mapper.userMapper.UserDtoMapper;
import com.bttf.queosk.mapper.userMapper.UserSignInMapper;
import com.bttf.queosk.repository.RefreshTokenRepository;
import com.bttf.queosk.repository.UserRepository;
import com.bttf.queosk.config.emailSender.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.bttf.queosk.exception.ErrorCode.*;
import static com.bttf.queosk.model.userModel.UserRole.ROLE_USER;
import static com.bttf.queosk.model.userModel.UserStatus.NOT_VERIFIED;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailSender emailSender;

    @Override
    @Transactional
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
                        .userRole(ROLE_USER)
                        .build()
        );
    }

    @Override
    @Transactional
    public UserSignInDto signInUser(UserSignInForm userSignInForm) {

        //입력된 email 로 사용자 조회
        User user = userRepository.findByEmail(userSignInForm.getEmail())
                .orElseThrow(() -> new CustomException(INVALID_USER_ID));

        //조회된 사용자의 비밀번호와 입력된 비밀번호 비교
        if (!passwordEncoder.matches(userSignInForm.getPassword(), user.getPassword())) {
            throw new CustomException(PASSWORD_NOT_MATCH);
        }

        //엑세스 토큰 발행
        String accessToken = jwtTokenProvider.generateAccessToken(
                TokenDtoMapper.INSTANCE.userToTokenDto(user)
        );

        //리프레시 토큰 발행
        String refreshToken = jwtTokenProvider.generateRefreshToken();

        //리프테시 토큰 저장(기존에 있었다면 덮어쓰기 - 성능상 조회 후 수정보다 덮어쓰기가 더 빠르고 가벼움)
        refreshTokenRepository.save(
                RefreshToken.builder()
                        .user_email(user.getEmail())
                        .refresh_token(refreshToken)
                        .build()
        );

        return UserSignInMapper.INSTANCE.userToUserSignInDto(user, refreshToken, accessToken);
    }

    @Override
    public boolean checkDuplication(String email) {
        return !userRepository.findByEmail(email).isPresent();
    }

    @Override
    public UserDto getUserFromToken(String token) {
        Long userId = jwtTokenProvider.getIdFromToken(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTS));

        return UserDtoMapper.INSTANCE.userToUserDto(user);
    }

    @Override
    public UserDto getUserFromId(Long id) {
        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTS));

        return UserDtoMapper.INSTANCE.userToUserDto(targetUser);
    }

    @Override
    @Transactional
    public UserDto editUserInformation(Long userId, UserEditForm userEditForm) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTS));

        user.editInformation(userEditForm);

        userRepository.save(user);

        return UserDtoMapper.INSTANCE.userToUserDto(user);
    }

    @Override
    @Transactional
    public void changeUserPassword(Long userId, UserPasswordChangeForm userPasswordChangeForm) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTS));

        if (!passwordEncoder.matches(userPasswordChangeForm.getExistingPassword(), user.getPassword())) {
            throw new CustomException(PASSWORD_NOT_MATCH);
        }

        user.changePassword(passwordEncoder.encode(userPasswordChangeForm.getNewPassword()));

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void resetUserPassword(String email, String nickName) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTS));

        if (!user.getNickName().equals(nickName)) {
            throw new CustomException(ErrorCode.NICKNAME_NOT_MATCH);
        }

        String randomPassword = UUID.randomUUID().toString().substring(0, 10);

        String encryptedPassword = passwordEncoder.encode(randomPassword);

        emailSender.sendEmail(
                user.getEmail(),
                "비밀번호 초기화 완료",
                "새로운 비밀번호 : " + randomPassword + "를 입력해 로그인 해주세요."
        );

        user.changePassword(encryptedPassword);
    }
}
