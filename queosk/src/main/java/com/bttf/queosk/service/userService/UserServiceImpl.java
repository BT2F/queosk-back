package com.bttf.queosk.service.userService;

import com.bttf.queosk.config.JwtTokenProvider;
import com.bttf.queosk.dto.userDto.UserDto;
import com.bttf.queosk.dto.userDto.UserSignInDto;
import com.bttf.queosk.dto.userDto.UserSignInForm;
import com.bttf.queosk.dto.userDto.UserSignUpForm;
import com.bttf.queosk.entity.RefreshToken;
import com.bttf.queosk.entity.User;
import com.bttf.queosk.exception.CustomException;
import com.bttf.queosk.exception.ErrorCode;
import com.bttf.queosk.mapper.userMapper.TokenDtoMapper;
import com.bttf.queosk.mapper.userMapper.UserDtoMapper;
import com.bttf.queosk.mapper.userMapper.UserSignInMapper;
import com.bttf.queosk.repository.RefreshTokenRepository;
import com.bttf.queosk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static com.bttf.queosk.exception.ErrorCode.*;
import static com.bttf.queosk.model.UserRole.ROLE_USER;
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
                        .userRole(ROLE_USER)
                        .build()
        );
    }

    @Override
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
}
