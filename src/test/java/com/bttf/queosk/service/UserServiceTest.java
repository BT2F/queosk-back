package com.bttf.queosk.service;

import com.bttf.queosk.config.JwtTokenProvider;
import com.bttf.queosk.dto.*;
import com.bttf.queosk.entity.User;
import com.bttf.queosk.exception.CustomException;
import com.bttf.queosk.exception.ErrorCode;
import com.bttf.queosk.repository.KakaoAuthRepository;
import com.bttf.queosk.repository.RefreshTokenRepository;
import com.bttf.queosk.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static com.bttf.queosk.enumerate.LoginType.NORMAL;
import static com.bttf.queosk.enumerate.UserRole.ROLE_USER;
import static com.bttf.queosk.enumerate.UserStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.mockito.Mockito.*;

@DisplayName("User 관련 테스트코드")
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private EmailSender emailSender;
    @Mock
    private KakaoAuthRepository kakaoAuthRepository;

    private UserLoginService userLoginService;
    private UserInfoService userInfoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userLoginService = new UserLoginService(
                userRepository, refreshTokenRepository,
                passwordEncoder, jwtTokenProvider, emailSender);
        userInfoService = new UserInfoService(
                userRepository, passwordEncoder, emailSender,
                refreshTokenRepository, kakaoAuthRepository);
    }

    @Test
    @DisplayName("사용자 생성 (성공)")
    void testCreateUser_Success() {
        // Given
        UserSignUpRequestForm userSignUpForm = UserSignUpRequestForm.builder()
                .email("test@example.com")
                .nickName("testUser")
                .password("password")
                .phone("123-456-7890")
                .build();

        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("encryptedPassword");
        when(passwordEncoder.matches(userSignUpForm.getPassword(), "encryptedPassword"))
                .thenReturn(true);

        // When
        userLoginService.createUser(userSignUpForm);

        // Then
        verify(userRepository, times(1)).findByEmail(eq("test@example.com"));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("사용자 생성 (실패-기존 사용자)")
    void testCreateUser_ExistingEmail() {
        // Given
        UserSignUpRequestForm userSignUpForm =
                UserSignUpRequestForm.builder()
                        .email("existing@example.com")
                        .build();

        when(userRepository.findByEmail(eq("existing@example.com")))
                .thenReturn(Optional.of(new User()));

        // When and Then
        assertThatThrownBy(() -> userLoginService.createUser(userSignUpForm))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("이미 가입된 회원입니다.");

        verify(userRepository, times(1)).findByEmail(eq("existing@example.com"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("사용자 로그인 (성공)")
    void signInUser_Success() {
        // given
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        User user = User.builder()
                .id(1L)
                .userRole(ROLE_USER)
                .imageUrl(null)
                .email("bzhs1992@icloud.com")
                .nickName("아무개")
                .password("test")
                .phone("01011111111")
                .status(VERIFIED)
                .loginType(NORMAL)
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtTokenProvider.generateAccessToken(any(TokenDto.class))).thenReturn(accessToken);
        when(jwtTokenProvider.generateRefreshToken(user.getEmail())).thenReturn(refreshToken);

        // when
        UserSignInDto result = userLoginService.signInUser(
                UserSignInRequestForm.builder()
                        .email(user.getEmail())
                        .password(user.getPassword())
                        .build()
        );

        // then
        assertNotNull(result);
        assertThat(user.getEmail()).isEqualTo(result.getEmail());
        assertThat(accessToken).isEqualTo(result.getAccessToken());
        assertThat(refreshToken).isEqualTo(result.getRefreshToken());
    }

    @Test
    @DisplayName("사용자 로그인 (실패-잘못된 아이디)")
    void signInUser_InvalidEmail() {
        // given
        String invalidEmail = "invalid@example.com";
        String password = "password";
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(
                () -> userLoginService.signInUser(
                        UserSignInRequestForm.builder()
                                .email(invalidEmail)
                                .password(password)
                                .build()))
                .isInstanceOf(CustomException.class)
                .hasMessage("존재하지 않는 아이디입니다.");
    }

    @Test
    @DisplayName("사용자 로그인 (실패-잘못된 비밀번호)")
    void signInUser_InvalidPassword() {
        // given
        String email = "user@example.com";
        String invalidPassword = "invalidPassword";
        String encodedPassword = "encodedPassword"; // Encoded password
        User user = User.builder()
                .email(email)
                .password(encodedPassword)
                .build();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // when & then
        assertThatThrownBy(
                () -> userLoginService.signInUser(
                        UserSignInRequestForm.builder()
                                .email(email)
                                .password(invalidPassword)
                                .build())
        )
                .isInstanceOf(CustomException.class)
                .hasMessage("비밀번호가 일치하지 않습니다.");
    }

    @Test
    @DisplayName("이메일 중복 확인 (성공)")
    public void testCheckDuplication_Success() {
        // Given
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        boolean result = userLoginService.checkDuplication(email);

        // Then
        assertThat(result).isTrue();
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("토큰 사용자 추출 (성공)")
    public void testGetUserFromToken_success() {
        // Given
        String token = "testToken";
        Long userId = 1L;
        User user = User.builder()
                .id(1L)
                .userRole(ROLE_USER)
                .imageUrl(null)
                .email("bzhs1992@icloud.com")
                .nickName("아무개")
                .password("test")
                .phone("01011111111")
                .status(VERIFIED)
                .loginType(NORMAL)
                .build();
        when(jwtTokenProvider.getIdFromToken(token)).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        UserDto userDto = userLoginService.getUserFromToken(token);

        // Then
        assertThat(userDto.getId()).isEqualTo(userId);
        verify(jwtTokenProvider, times(1)).getIdFromToken(token);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("토큰 사용자 추출 (실패-사용자 정보 없음)")
    public void testGetUserFromToken_UserNotExist() {
        // Given
        String token = "testToken";
        Long userId = 1L;
        when(jwtTokenProvider.getIdFromToken(token)).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When, Then
        assertThatThrownBy(() -> userLoginService.getUserFromToken(token))
                .isInstanceOf(CustomException.class)
                .hasMessage("해당 회원을 찾을 수 없습니다.");
        verify(jwtTokenProvider, times(1)).getIdFromToken(token);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("ID로 사용자 찾기 (성공)")
    public void testGetUserFromId() {
        // Given
        Long userId = 1L;
        User user = User.builder()
                .id(1L)
                .userRole(ROLE_USER)
                .imageUrl(null)
                .email("bzhs1992@icloud.com")
                .nickName("아무개")
                .password("test")
                .phone("01011111111")
                .status(VERIFIED)
                .loginType(NORMAL)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        UserDto userDto = userLoginService.getUserFromId(userId);

        // Then
        assertThat(userDto.getId()).isEqualTo(user.getId());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("ID로 사용자 찾기 (실패-사용자 정보 없음)")
    public void testGetUserFromId_UserNotExist() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When, Then
        assertThatThrownBy(() -> userLoginService.getUserFromId(userId))
                .isInstanceOf(CustomException.class)
                .hasMessage("해당 회원을 찾을 수 없습니다.");
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("사용자정보 수정 (성공)")
    public void testEditUserInformation_Success() {
        // Given
        Long userId = 1L;
        UserEditRequestForm userEditForm = UserEditRequestForm.builder()
                .nickName("나나난")
                .phone("01099998888")
                .build();
        User user = User.builder()
                .id(userId)
                .userRole(ROLE_USER)
                .imageUrl(null)
                .email("bzhs1992@icloud.com")
                .nickName("아무개")
                .password("test")
                .phone("01011111111")
                .status(VERIFIED)
                .loginType(NORMAL)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        // When
        UserDto userDto = userInfoService.editUserInformation(userId, userEditForm);

        // Then
        assertThat(userDto.getId()).isEqualTo(userId);
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("사용자정보 수정 (실패-존재하지 않는 사용자)")
    public void testEditUserInformation_UserNotExist() {
        // Given
        Long userId = 1L;
        UserEditRequestForm userEditForm = new UserEditRequestForm();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When, Then
        assertThatThrownBy(() -> userInfoService.editUserInformation(userId, userEditForm))
                .isInstanceOf(CustomException.class)
                .hasMessage("해당 회원을 찾을 수 없습니다.");
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("사용자 비밀번호 변경 (성공)")
    public void testChangeUserPassword_Success() {
        // Given
        Long userId = 1L;
        String existingPassword = "oldPassword";
        String newPassword = "newPassword";

        User user = User.builder()
                .id(userId)
                .loginType(NORMAL)
                .password("encodedOldPassword")
                .build();

        UserPasswordChangeRequestForm userPasswordChangeForm =
                UserPasswordChangeRequestForm.builder()
                        .existingPassword("oldPassword")
                        .newPassword(newPassword)
                        .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(existingPassword, user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");

        // When
        userInfoService.changeUserPassword(userId, userPasswordChangeForm);

        // Then
        verify(userRepository).findById(userId);
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(user);

        assertThat(user.getPassword()).isEqualTo("encodedNewPassword");
    }

    @Test
    @DisplayName("사용자 비밀번호 변경 (실패-기존비밀번호오류)")
    public void testChangeUserPassword_ExistingPassword() {
        // Given
        Long userId = 1L;
        String existingPassword = "invalidOldPassword";
        String newPassword = "newPassword";

        User user = User.builder()
                .id(userId)
                .loginType(NORMAL)
                .password("encodedOldPassword")
                .build();

        UserPasswordChangeRequestForm userPasswordChangeForm =
                UserPasswordChangeRequestForm.builder()
                        .existingPassword("invalidOldPassword")
                        .newPassword(newPassword)
                        .build();

        // Mock userRepository
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(existingPassword, user.getPassword())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> userInfoService.changeUserPassword(userId, userPasswordChangeForm))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.PASSWORD_NOT_MATCH.getMessage());

        verify(userRepository).findById(userId);
        verify(passwordEncoder).matches(existingPassword, user.getPassword());
        verifyNoMoreInteractions(passwordEncoder, userRepository);
    }

    @Test
    @DisplayName("사용자 비밀번호 변경 (실패-회원정보없음)")
    public void testChangeUserPassword_UserNotFound() {
        // Given
        Long userId = 1L;

        UserPasswordChangeRequestForm userPasswordChangeForm =
                UserPasswordChangeRequestForm.builder()
                        .existingPassword("oldPassword")
                        .newPassword("newPassword")
                        .build();

        // Mock userRepository
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userInfoService.changeUserPassword(userId, userPasswordChangeForm))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.USER_NOT_EXISTS.getMessage());

        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(passwordEncoder, userRepository);
    }

    @Test
    @DisplayName("사용자 비밀번호 초기화 (성공)")
    public void testResetUserPassword_Success() {
        // Given
        User user = User.builder()
                .email("user@example.com")
                .nickName("testuser")
                .loginType(NORMAL)
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // When
        userInfoService.resetUserPassword("user@example.com", "testuser");

        // Then
        verify(emailSender).sendEmail(eq("user@example.com"), anyString(), anyString());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("사용자 비밀번호 초기화 (실패-사용자정보없음)")
    public void testResetUserPassword_UserNotExists() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // When, Then
        assertThatThrownBy(() -> userInfoService.resetUserPassword("user@example.com", "testuser"))
                .isInstanceOf(CustomException.class);
        verify(emailSender, never()).sendEmail(anyString(), anyString(), anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("사용자 비밀번호 초기화 (실패-닉네임불일치)")
    public void testResetUserPassword_NickNameNotMatch() {
        // Given
        User user = User.builder()
                .email("user@example.com")
                .nickName("testuser")
                .loginType(NORMAL)
                .build();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        // When, Then
        assertThatThrownBy(() ->
                userInfoService.resetUserPassword("user@example.com", "wrongnick"))
                .isInstanceOf(CustomException.class);
        verify(emailSender, never()).sendEmail(anyString(), anyString(), anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("사용자 이미지 업로드 (성공)")
    public void testUpdateImageUrl_Success() {
        // Given
        User user = User.builder().id(1L).build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        // When
        userInfoService.updateImageUrl(1L, "newImageUrl");

        // Then
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("사용자 이미지 업로드 (성공)")
    public void testUpdateImageUrl_UserNotExists() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When, Then
        assertThatThrownBy(() -> userInfoService.updateImageUrl(1L, "newImageUrl"))
                .isInstanceOf(CustomException.class);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("사용자 탈퇴 (성공)")
    public void testWithdrawUser_Success() {
        // Given
        User user = User.builder()
                .id(1L)
                .loginType(NORMAL)
                .password(passwordEncoder.encode("correctPassword"))
                .build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("correctPassword", user.getPassword()))
                .thenReturn(true);

        // When
        userInfoService.withdrawUser(1L);

        // Then
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("사용자 탈퇴 (실패-사용자 정보없음)")
    public void testWithdrawUser_UserNotExists() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When, Then
        assertThatThrownBy(() -> userInfoService.withdrawUser(1L))
                .isInstanceOf(CustomException.class);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("사용자 탈퇴 (실패-비밀번호오류)")
    public void testWithdrawUser_IncorrectPassword() {
        // Given
        User user = User.builder()
                .id(1L)
                .password(passwordEncoder.encode("correctPassword"))
                .build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        // When, Then
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("사용자 이메일인증 (성공)")
    public void testVerifyUser_Success() {
        // Given
        User user = User.builder().id(1L).status(NOT_VERIFIED).build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        // When
        String result = userInfoService.verifyUser(1L);

        // Then
        verify(userRepository).save(user);
        assertThat(result).isEqualTo("인증이 완료되었습니다.");
    }

    @Test
    @DisplayName("사용자 이메일인증 (실패-사용자정보없음)")
    public void testVerifyUser_UserNotExists() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When, Then
        assertThatThrownBy(() -> userInfoService.verifyUser(1L))
                .isInstanceOf(CustomException.class);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("사용자 이메일인증 (실패-탈퇴된사용자)")
    public void testVerifyUser_UserDeleted() {
        // Given
        User user = User.builder().id(1L).status(DELETED).build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        // When
        String result = userInfoService.verifyUser(1L);

        // Then
        verify(userRepository, never()).save(any());
        assertThat(result).isEqualTo("탈퇴한 회원입니다.");
    }

    @Test
    @DisplayName("사용자 이메일인증 (실패-이미인증된사용자)")
    public void testVerifyUser_UserAlreadyVerified() {
        // Given
        User user = User.builder().id(1L).status(VERIFIED).build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        // When
        String result = userInfoService.verifyUser(1L);

        // Then
        verify(userRepository, never()).save(any());
        assertThat(result).isEqualTo("이미 인증이 완료된 회원입니다.");
    }
}