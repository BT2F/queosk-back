package com.bttf.queosk.service.userService;

import com.bttf.queosk.config.emailSender.EmailSender;
import com.bttf.queosk.config.springSecurity.JwtTokenProvider;
import com.bttf.queosk.dto.tokenDto.TokenDto;
import com.bttf.queosk.dto.userDto.*;
import com.bttf.queosk.entity.RefreshToken;
import com.bttf.queosk.entity.User;
import com.bttf.queosk.exception.CustomException;
import com.bttf.queosk.exception.ErrorCode;
import com.bttf.queosk.repository.RefreshTokenRepository;
import com.bttf.queosk.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static com.bttf.queosk.model.userModel.UserStatus.VERIFIED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

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

    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(
                userRepository, refreshTokenRepository,
                passwordEncoder, jwtTokenProvider, emailSender);
    }

    @Test
    void testCreateUser() {
        // Given
        UserSignUpForm userSignUpForm = UserSignUpForm.builder()
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
        userService.createUser(userSignUpForm);

        // Then
        verify(userRepository, times(1)).findByEmail(eq("test@example.com"));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUserWithExistingEmail() {
        // Given
        UserSignUpForm userSignUpForm =
                UserSignUpForm.builder()
                        .email("existing@example.com")
                        .build();

        when(userRepository.findByEmail(eq("existing@example.com")))
                .thenReturn(Optional.of(new User()));

        // When and Then
        assertThatThrownBy(() -> userService.createUser(userSignUpForm))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("이미 가입된 회원입니다.");

        verify(userRepository, times(1)).findByEmail(eq("existing@example.com"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void signInUser_Success() {
        // Arrange
        String email = "user@example.com";
        String password = "password";
        String encodedPassword = "encodedPassword"; // Encoded password
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        User user = User.builder()
                .email(email)
                .status(VERIFIED)
                .password(encodedPassword)
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtTokenProvider.generateAccessToken(any(TokenDto.class))).thenReturn(accessToken);
        when(jwtTokenProvider.generateRefreshToken()).thenReturn(refreshToken);

        // Act
        UserSignInDto result = userService.signInUser(new UserSignInForm(email, password));

        // Assert
        assertNotNull(result);
        assertThat(email).isEqualTo(result.getEmail());
        assertThat(accessToken).isEqualTo(result.getAccessToken());
        assertThat(refreshToken).isEqualTo(result.getRefreshToken());
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    void signInUser_InvalidEmail() {
        // Arrange
        String invalidEmail = "invalid@example.com";
        String password = "password";
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomException.class, () -> {
            userService.signInUser(new UserSignInForm(invalidEmail, password));
        });
    }

    @Test
    void signInUser_InvalidPassword() {
        // Arrange
        String email = "user@example.com";
        String invalidPassword = "invalidPassword";
        String encodedPassword = "encodedPassword"; // Encoded password
        User user = User.builder()
                .email(email)
                .password(encodedPassword)
                .build();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // Act & Assert
        assertThrows(CustomException.class, () -> {
            userService.signInUser(new UserSignInForm(email, invalidPassword));
        });
    }

    @Test
    public void testCheckDuplication() {
        // Given
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        boolean result = userService.checkDuplication(email);

        // Then
        assertThat(result).isTrue();
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    public void testGetUserFromToken() {
        // Given
        String token = "testToken";
        Long userId = 1L;
        User user = User.builder().id(1L).build();
        when(jwtTokenProvider.getIdFromToken(token)).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        UserDto userDto = userService.getUserFromToken(token);

        // Then
        assertThat(userDto.getId()).isEqualTo(userId);
        verify(jwtTokenProvider, times(1)).getIdFromToken(token);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testGetUserFromTokenThrowsExceptionWhenUserNotExists() {
        // Given
        String token = "testToken";
        Long userId = 1L;
        when(jwtTokenProvider.getIdFromToken(token)).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When, Then
        assertThrows(CustomException.class, () -> userService.getUserFromToken(token));
        verify(jwtTokenProvider, times(1)).getIdFromToken(token);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testGetUserFromId() {
        // Given
        Long userId = 1L;
        User user = User.builder().id(1L).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        UserDto userDto = userService.getUserFromId(userId);

        // Then
        assertThat(userDto.getId()).isEqualTo(userId);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testGetUserFromIdThrowsExceptionWhenUserNotExists() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When, Then
        assertThrows(CustomException.class, () -> userService.getUserFromId(userId));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testEditUserInformation() {
        // Given
        Long userId = 1L;
        UserEditForm userEditForm = new UserEditForm();
        User user = User.builder().id(1L).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        UserDto userDto = userService.editUserInformation(userId, userEditForm);

        // Then
        assertThat(userDto.getId()).isEqualTo(userId);
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testEditUserInformationThrowsExceptionWhenUserNotExists() {
        // Given
        Long userId = 1L;
        UserEditForm userEditForm = new UserEditForm();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When, Then
        assertThrows(CustomException.class, () -> userService.editUserInformation(userId, userEditForm));
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testChangeUserPassword() {
        // Given
        Long userId = 1L;
        String existingPassword = "oldPassword";
        String newPassword = "newPassword";

        User user = User.builder().id(userId).password("encodedOldPassword").build();

        UserPasswordChangeForm userPasswordChangeForm =
                UserPasswordChangeForm.builder()
                        .existingPassword("oldPassword")
                        .newPassword(newPassword)
                        .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(existingPassword, user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");

        // When
        userService.changeUserPassword(userId, userPasswordChangeForm);

        // Then
        verify(userRepository).findById(userId);
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(user);

        assertThat(user.getPassword()).isEqualTo("encodedNewPassword");
    }

    @Test
    public void testChangeUserPasswordInvalidExistingPassword() {
        // Given
        Long userId = 1L;
        String existingPassword = "invalidOldPassword";
        String newPassword = "newPassword";

        User user = User.builder().id(userId).password("encodedOldPassword").build();

        UserPasswordChangeForm userPasswordChangeForm =
                UserPasswordChangeForm.builder()
                        .existingPassword("invalidOldPassword")
                        .newPassword(newPassword)
                        .build();

        // Mock userRepository
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(existingPassword, user.getPassword())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> userService.changeUserPassword(userId, userPasswordChangeForm))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.PASSWORD_NOT_MATCH.getMessage());

        verify(userRepository).findById(userId);
        verify(passwordEncoder).matches(existingPassword, user.getPassword());
        verifyNoMoreInteractions(passwordEncoder, userRepository);
    }

    @Test
    public void testChangeUserPasswordUserNotFound() {
        // Given
        Long userId = 1L;

        UserPasswordChangeForm userPasswordChangeForm =
                UserPasswordChangeForm.builder()
                        .existingPassword("oldPassword")
                        .newPassword("newPassword")
                        .build();

        // Mock userRepository
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.changeUserPassword(userId, userPasswordChangeForm))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.USER_NOT_EXISTS.getMessage());

        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(passwordEncoder, userRepository);
    }
    @Test
    public void testResetUserPassword_Success() {
        // Given
        User user = User.builder().email("user@example.com").nickName("testuser").build();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // When
        userService.resetUserPassword("user@example.com", "testuser");

        // Then
        verify(emailSender).sendEmail(eq("user@example.com"), anyString(), anyString());
        verify(userRepository).save(user);
    }
    @Test
    public void testResetUserPassword_UserNotExists() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // When, Then
        Assertions.assertThatThrownBy(() -> userService.resetUserPassword("user@example.com", "testuser"))
                .isInstanceOf(CustomException.class);
        verify(emailSender, never()).sendEmail(anyString(), anyString(), anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    public void testResetUserPassword_NickNameNotMatch() {
        // Given
        User user = User.builder()
                .email("user@example.com")
                .nickName("testuser")
                .build();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        // When, Then
        assertThatThrownBy(() -> userService.resetUserPassword("user@example.com", "wrongnick"))
                .isInstanceOf(CustomException.class);
        verify(emailSender, never()).sendEmail(anyString(), anyString(), anyString());
        verify(userRepository, never()).save(any());
    }
}