package com.bttf.queosk.service;

import com.bttf.queosk.config.springSecurity.JwtTokenProvider;
import com.bttf.queosk.controller.RestaurantController;
import com.bttf.queosk.dto.enumerate.RestaurantCategory;
import com.bttf.queosk.dto.restaurantDto.RestaurantSignInDTO;
import com.bttf.queosk.dto.restaurantDto.RestaurantSignInForm;
import com.bttf.queosk.dto.restaurantDto.RestaurantSignUpForm;
import com.bttf.queosk.dto.tokenDto.TokenDto;
import com.bttf.queosk.entity.RefreshToken;
import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.repository.RefreshTokenRepository;
import com.bttf.queosk.repository.RestaurantRepository;
import com.bttf.queosk.service.RestaurantService.RestaurantService;
import com.bttf.queosk.util.KakaoGeoAddress;
import com.google.gson.Gson;
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
    private KakaoGeoAddress kakaoGeoAddress;

    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(restaurantController).build();
        restaurantService = new RestaurantService(restaurantRepository, refreshTokenRepository, passwordEncoder, jwtTokenProvider, kakaoGeoAddress);
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

        RestaurantSignInDTO result = restaurantService.signIn(restaurantSignInForm);


        // then

        assertNotNull(result);
        assertThat(id).isEqualTo(result.getOwnerId());
        assertThat(accessToken).isEqualTo(result.getAccessToken());
        assertThat(refreshToken).isEqualTo(result.getRefreshToken());
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

}