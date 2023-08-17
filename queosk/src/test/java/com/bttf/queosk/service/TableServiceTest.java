package com.bttf.queosk.service;

import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.exception.CustomException;
import com.bttf.queosk.repository.RestaurantRepository;
import com.bttf.queosk.repository.TableRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;


@Transactional
@ExtendWith(MockitoExtension.class)
class TableServiceTest {

    @Spy
    @InjectMocks
    private TableService tableService;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private TableRepository tableRepository;

    @Test
    @DisplayName("테이블 생성 테스트 - 성공 케이스")
    public void successCreateTable() {
        //given
        Long restaurantId = 1L;
        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .build();

        given(restaurantRepository.findById(anyLong())).willReturn(Optional.of(restaurant));

        //when
        tableService.createTable(restaurantId);

        //then
        then(tableService).should(times(1)).createTable(restaurantId);

    }

    @Test
    @DisplayName("테이블 생성 테스트 - restaurantId가 없을 경우")
    public void failCreateTableNotFoundRestaurantId() {
        //given
        Long restaurantId = 1L;
        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .build();

        given(restaurantRepository.findById(anyLong())).willReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> tableService.createTable(restaurantId))
                .isExactlyInstanceOf(CustomException.class);
        then(tableService).should(times(1)).createTable(restaurantId);
    }
}