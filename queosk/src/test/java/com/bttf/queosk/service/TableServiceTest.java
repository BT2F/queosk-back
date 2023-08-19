package com.bttf.queosk.service;

import com.bttf.queosk.dto.enumerate.TableStatus;
import com.bttf.queosk.dto.tableDto.TableForm;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    @DisplayName("테이블 생성 테스트 - restaurant 존재하지 않는 경우")
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
        Long restaurantId = 1L;
        //given
        Table table = Table.builder()
                .id(1L)
                .status(TableStatus.OPEN)
                .restaurantId(1L)
                .build();

        given(tableRepository.findById(table.getId())).willReturn(Optional.of(table));

        //when
        tableService.updateTable(table.getId(), TableStatus.USING, restaurantId);

        //then
        then(tableService).should(times(1)).updateTable(table.getId(), TableStatus.USING, restaurantId);
    }

    @Test
    @DisplayName("테이블 수정 테스트 - table 찾을 수 없을 경우")
    public void testUpdateTable_fail_invalidTableException() {
        //given
        Table table = Table.builder()
                .id(1L)
                .status(TableStatus.OPEN)
                .restaurantId(1L)
                .build();

        given(tableRepository.findById(anyLong())).willReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> tableService.updateTable(table.getId(), TableStatus.USING, table.getRestaurantId()))
                .isExactlyInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_TABLE.getMessage());
        then(tableService).should(times(1)).updateTable(table.getId(), TableStatus.USING, table.getRestaurantId());
    }

    @Test
    @DisplayName("테이블 수정 테스트 - 본인 restaurant 의 table 이 아닌 경우")
    public void testUpdateTable_fail_NotPermittedTableException() {
        //given
        Table table = Table.builder()
                .id(1L)
                .status(TableStatus.OPEN)
                .restaurantId(1L)
                .build();

        given(tableRepository.findById(table.getId())).willReturn(Optional.of(table));

        //then
        assertThatThrownBy(() -> tableService.updateTable(table.getId(), TableStatus.USING, 2L))
                .isExactlyInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.NOT_PERMITTED.getMessage());
        then(tableService).should(times(1)).updateTable(table.getId(), TableStatus.USING, 2L);
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
        tableService.deleteTable(table.getId(), table.getRestaurantId());

        //then
        then(tableService).should(times(1)).deleteTable(table.getId(), table.getRestaurantId());

    }

    @Test
    @DisplayName("테이블 삭제 테스트 - table 찾을 수 없을 경우")
    public void testDeleteTable_fail_invalidTableException() {
        //given
        Table table = Table.builder()
                .id(1L)
                .status(TableStatus.OPEN)
                .restaurantId(1L)
                .build();

        given(tableRepository.findById(anyLong())).willReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> tableService.deleteTable(table.getId(), table.getRestaurantId()))
                .isExactlyInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_TABLE.getMessage());
        then(tableService).should(times(1)).deleteTable(table.getId(), table.getRestaurantId());
    }

    @Test
    @DisplayName("테이블 삭제 테스트 - 본인 restaurant 의 table 이 아닌 경우")
    public void testDeleteTable_fail_NotPermittedException() {
        //given
        Table table = Table.builder()
                .id(1L)
                .status(TableStatus.OPEN)
                .restaurantId(1L)
                .build();

        given(tableRepository.findById(anyLong())).willReturn(Optional.of(table));

        //then
        assertThatThrownBy(() -> tableService.deleteTable(table.getId(), 2L))
                .isExactlyInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.NOT_PERMITTED.getMessage());
        then(tableService).should(times(1)).deleteTable(table.getId(), 2L);
    }

    @Test
    @DisplayName("테이블 가져오기 테스트 - 성공 케이스")
    public void testGetTable_success() {
        Long tableId = 1L;
        Long restaurantId = 1L;
        Table table = Table.builder()
                .id(tableId)
                .status(TableStatus.OPEN)
                .restaurantId(restaurantId)
                .build();

        given(tableRepository.findById(table.getId())).willReturn(Optional.of(table));

        // When
        TableForm table1 = tableService.getTable(table.getId(), restaurantId);

        // Then
        then(tableService).should(times(1)).getTable(table.getId(), restaurantId);
        assertThat(table1.getTableId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("테이블 가져오기 테스트 - 본인 restaurant 의 table 이 아닌 경우")
    public void testGetTable_fail_NotPermittedException() {
        Long tableId = 1L;
        Long restaurantId = 1L;
        Table table = Table.builder()
                .id(tableId)
                .status(TableStatus.OPEN)
                .restaurantId(restaurantId)
                .build();

        given(tableRepository.findById(table.getId())).willReturn(Optional.of(table));

        // When
        TableForm table1 = tableService.getTable(table.getId(), restaurantId);

        assertThatThrownBy(() -> tableService.getTable(table.getId(), 2L))
                .isExactlyInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.NOT_PERMITTED.getMessage());
        then(tableService).should(times(1)).getTable(table.getId(), 2L);
    }
}