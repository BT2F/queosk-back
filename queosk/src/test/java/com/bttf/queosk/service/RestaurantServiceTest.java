package com.bttf.queosk.service;

import com.bttf.queosk.controller.RestaurantController;
import com.bttf.queosk.domain.RestaurantSignInForm;
import com.bttf.queosk.domain.enumerate.RestaurantCategory;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@Rollback
@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @InjectMocks
    private RestaurantController restaurantController;

    @Mock
    private RestaurantService restaurantService;

    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(restaurantController).build();
    }

    @DisplayName("매장 생성 테스트")
    @Test
    public void 매장_생성() throws Exception {
        // given

        RestaurantSignInForm restaurantSignInForm =
                RestaurantSignInForm.builder()
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
                        .address("경기도 부천시 소사로276번길 63")
                        .build();


        // when
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.post(
                        "/api/restaurant/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(restaurantSignInForm))
        );

        // then

        MvcResult mvcResult =
                actions.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                        .andReturn();

        String id = mvcResult.getResponse().getContentAsString();

        assertThat(id).isNotNull();
    }
}