package com.bttf.queosk.service;

import com.bttf.queosk.service.emailsender.EmailSender;
import com.bttf.queosk.config.springsecurity.JwtTokenProvider;
import com.bttf.queosk.controller.RestaurantController;
import com.bttf.queosk.enumerate.RestaurantCategory;
import com.bttf.queosk.dto.restaurantdto.RestaurantSignInDto;
import com.bttf.queosk.dto.restaurantdto.RestaurantSignInForm;
import com.bttf.queosk.dto.restaurantdto.RestaurantSignUpForm;
import com.bttf.queosk.dto.restaurantdto.RestaurantUpdatePasswordForm;
import com.bttf.queosk.dto.tokendto.TokenDto;
import com.bttf.queosk.entity.RefreshToken;
import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.exception.CustomException;
import com.bttf.queosk.exception.ErrorCode;
import com.bttf.queosk.repository.RefreshTokenRepository;
import com.bttf.queosk.repository.RestaurantRepository;
import com.bttf.queosk.service.kakaoservice.KakaoGeoAddressService;
import com.bttf.queosk.service.imageservice.ImageService;
import com.bttf.queosk.service.restaurantservice.RestaurantService;
import com.google.gson.Gson;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Transactional
@Rollback
@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @InjectMocks
    private RestaurantController restaurantController;
    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private RestaurantService restaurantService;
    @Mock
    private KakaoGeoAddressService kakaoGeoAddressService;
    @Mock
    private ImageService imageService;
    @Mock
    private EmailSender emailSender;

    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(restaurantController).build();
        restaurantService = new RestaurantService(restaurantRepository, refreshTokenRepository,
                passwordEncoder, jwtTokenProvider, kakaoGeoAddressService, imageService, emailSender);
    }

    @DisplayName("매장 생성 테스트")
    @Test
    public void 매장_생성() throws Exception {
        // given

        RestaurantSignUpForm restaurantSignUpForm =
                RestaurantSignUpForm.builder()
                        .ownerId("test")
                        .ownerName("test")
                        .password("1234")
                        .email("a@x.com")
                        .phone("01001234567")
                        .restaurantPhone("0101234567")
                        .restaurantName("테스트네 식당")
                        .category(RestaurantCategory.ASIAN)
                        .businessNumber("123-45-67890")
                        .businessStartDate("19900428")
                        .address("청와대로 1")
                        .build();


        // when
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.post(
                        "/api/restaurant/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(restaurantSignUpForm))
        );

        // then

        MvcResult mvcResult =
                actions.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                        .andReturn();

        String id = mvcResult.getResponse().getContentAsString();

        assertThat(id).isNotNull();
    }

    @Test
    public void RestaurantSignIn_Test() throws Exception {
        // given

        String id = "test";
        String password = "password";
        String encodedPassword = "encodedPassword"; // Encoded password
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";

        RestaurantSignInForm restaurantSignInForm = RestaurantSignInForm
                .builder()
                .ownerId(id)
                .password(password)
                .build();

        Restaurant restaurant = Restaurant.builder()
                .ownerId(id)
                .password(encodedPassword)
                .build();

        // when

        when(restaurantRepository.findByOwnerId(anyString())).thenReturn(Optional.of(restaurant));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtTokenProvider.generateAccessToken(any(TokenDto.class))).thenReturn(accessToken);
        when(jwtTokenProvider.generateRefreshToken()).thenReturn(refreshToken);

        RestaurantSignInDto result = restaurantService.signIn(restaurantSignInForm);


        // then

        assertNotNull(result);
        assertThat(id).isEqualTo(result.getOwnerId());
        assertThat(accessToken).isEqualTo(result.getAccessToken());
        assertThat(refreshToken).isEqualTo(result.getRefreshToken());
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    public void testUpdateRestaurantPassword() {
        // Given
        Long restaurantId = 1L;
        String existingPassword = "oldPassword";
        String newPassword = "newPassword";

        Restaurant restaurant = Restaurant.builder().id(restaurantId).password("encodedOldPassword").build();

        RestaurantUpdatePasswordForm restaurantUpdatePasswordForm =
                RestaurantUpdatePasswordForm.builder()
                        .oldPassword("oldPassword")
                        .newPassword(newPassword)
                        .build();

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(passwordEncoder.matches(existingPassword, restaurant.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");

        // When
        restaurantService.updateRestaurantPassword(restaurantId, restaurantUpdatePasswordForm);

        // Then
        verify(restaurantRepository).findById(restaurantId);
        verify(passwordEncoder).encode(newPassword);
        verify(restaurantRepository).save(restaurant);

        assertThat(restaurant.getPassword()).isEqualTo("encodedNewPassword");
    }

    @Test
    public void testUpdateRestaurantPasswordInvalidExistingPassword() {
        // Given
        Long restaurantId = 1L;
        String existingPassword = "invalidOldPassword";
        String newPassword = "newPassword";

        Restaurant restaurant = Restaurant.builder().id(restaurantId).password("encodedOldPassword").build();

        RestaurantUpdatePasswordForm restaurantUpdatePasswordForm =
                RestaurantUpdatePasswordForm.builder()
                        .oldPassword("invalidOldPassword")
                        .newPassword(newPassword)
                        .build();

        // Mock userRepository
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(passwordEncoder.matches(existingPassword, restaurant.getPassword())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> restaurantService.updateRestaurantPassword(restaurantId, restaurantUpdatePasswordForm))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.PASSWORD_NOT_MATCH.getMessage());

        verify(restaurantRepository).findById(restaurantId);
        verify(passwordEncoder).matches(existingPassword, restaurant.getPassword());
        verifyNoMoreInteractions(passwordEncoder, restaurantRepository);
    }

    @Test
    public void testUpdateRestaurantPasswordRestaurantNotFound() {
        // Given
        Long restaurant = 1L;

        RestaurantUpdatePasswordForm restaurantUpdatePasswordForm =
                RestaurantUpdatePasswordForm.builder()
                        .oldPassword("oldPassword")
                        .newPassword("newPassword")
                        .build();

        // Mock userRepository
        when(restaurantRepository.findById(restaurant)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> restaurantService.updateRestaurantPassword(restaurant, restaurantUpdatePasswordForm))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_RESTAURANT.getMessage());

        verify(restaurantRepository).findById(restaurant);
        verifyNoMoreInteractions(passwordEncoder, restaurantRepository);
    }

    @Test
    public void testResetRestaurantPassword_Success() {
        // Given
        Restaurant restaurant = Restaurant.builder().email("user@example.com").ownerName("testuser").build();
        when(restaurantRepository.findByEmail(anyString())).thenReturn(Optional.of(restaurant));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // When
        restaurantService.resetRestaurantPassword("user@example.com", "testuser");

        // Then
        verify(emailSender).sendEmail(eq("user@example.com"), anyString(), anyString());
        verify(restaurantRepository).save(restaurant);
    }

    @Test
    public void testResetRestaurantPassword_UserNotExists() {
        // Given
        when(restaurantRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // When, Then
        Assertions.assertThatThrownBy(() -> restaurantService.resetRestaurantPassword("user@example.com", "testuser"))
                .isInstanceOf(CustomException.class);
        verify(emailSender, never()).sendEmail(anyString(), anyString(), anyString());
        verify(restaurantRepository, never()).save(any());
    }

    @Test
    public void testResetUserPassword_OwnerNameNotMatch() {
        // Given
        Restaurant restaurant = Restaurant.builder()
                .email("user@example.com")
                .ownerName("testuser")
                .build();
        when(restaurantRepository.findByEmail(anyString())).thenReturn(Optional.of(restaurant));

        // When, Then
        assertThatThrownBy(() -> restaurantService.resetRestaurantPassword("user@example.com", "wrongname"))
                .isInstanceOf(CustomException.class);
        verify(emailSender, never()).sendEmail(anyString(), anyString(), anyString());
        verify(restaurantRepository, never()).save(any());
    }
}