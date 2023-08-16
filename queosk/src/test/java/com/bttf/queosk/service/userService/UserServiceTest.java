package com.bttf.queosk.service.userService;

import com.bttf.queosk.config.JwtTokenProvider;

import com.bttf.queosk.dto.tokenDto.TokenDto;
import com.bttf.queosk.dto.userDto.UserSignInDto;
import com.bttf.queosk.dto.userDto.UserSignInForm;
import com.bttf.queosk.dto.userDto.UserSignUpForm;
import com.bttf.queosk.entity.RefreshToken;
import com.bttf.queosk.entity.User;
import com.bttf.queosk.exception.CustomException;
import com.bttf.queosk.repository.RefreshTokenRepository;
import com.bttf.queosk.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository, refreshTokenRepository, passwordEncoder, jwtTokenProvider);
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
}