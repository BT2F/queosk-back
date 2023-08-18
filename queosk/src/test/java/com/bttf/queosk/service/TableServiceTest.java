package com.bttf.queosk.service;

import com.bttf.queosk.dto.enumerate.TableStatus;
import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.entity.Table;
import com.bttf.queosk.exception.CustomException;
import com.bttf.queosk.exception.ErrorCode;
import com.bttf.queosk.repository.RestaurantRepository;
import com.bttf.queosk.repository.TableRepository;
import com.bttf.queosk.service.tableService.TableService;
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
    public void testCreateTable_success() {
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
    public void testCreateTable_fail_invalidRestaurantException() {
        //given
        Long restaurantId = 1L;
        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .build();

        given(restaurantRepository.findById(anyLong())).willReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> tableService.createTable(restaurantId))
                .isExactlyInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_RESTAURANT.getMessage());
        then(tableService).should(times(1)).createTable(restaurantId);
    }

    @Test
    @DisplayName("테이블 수정 테스트 - 성공 케이스")
    public void testUpdateTable_success() {
        //given
        Table table = Table.builder()
                .id(1L)
                .status(TableStatus.OPEN)
                .restaurantId(1L)
                .build();

        given(tableRepository.findById(table.getId())).willReturn(Optional.of(table));

        //when
        tableService.updateTable(table.getId(), TableStatus.USING);

        //then
        then(tableService).should(times(1)).updateTable(table.getId(), TableStatus.USING);

    }

    @Test
    @DisplayName("테이블 수정 테스트 - tableId가 없을 경우")
    public void testUpdateTable_fail_invalidTableException() {
        //given
        Table table = Table.builder()
                .id(1L)
                .status(TableStatus.OPEN)
                .restaurantId(1L)
                .build();

        given(tableRepository.findById(anyLong())).willReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> tableService.updateTable(table.getId(), TableStatus.USING))
                .isExactlyInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_TABLE.getMessage());
        then(tableService).should(times(1)).updateTable(table.getId(), TableStatus.USING);
    }

    @Test
    @DisplayName("테이블 삭제 테스트 - 성공 케이스")
    public void testDeleteTable_success() {
        //given
        Table table = Table.builder()
                .id(1L)
                .status(TableStatus.OPEN)
                .restaurantId(1L)
                .build();

        given(tableRepository.findById(table.getId())).willReturn(Optional.of(table));

        //when
        tableService.deleteTable(table.getId());

        //then
        then(tableService).should(times(1)).deleteTable(table.getId());

    }
    @Test
    @DisplayName("테이블 삭제 테스트 - tableId가 없을 경우")
    public void testDeleteTable_fail_invalidTableException() {
        //given
        Table table = Table.builder()
                .id(1L)
                .status(TableStatus.OPEN)
                .restaurantId(1L)
                .build();

        given(tableRepository.findById(anyLong())).willReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> tableService.deleteTable(table.getId()))
                .isExactlyInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_TABLE.getMessage());
        then(tableService).should(times(1)).deleteTable(table.getId());
    }
}