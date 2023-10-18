package com.bttf.queosk.service;

import com.bttf.queosk.dto.TableDto;
import com.bttf.queosk.dto.TableNameUpdateForm;
import com.bttf.queosk.dto.TableRequestForm;
import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.entity.Table;
import com.bttf.queosk.enumerate.TableStatus;
import com.bttf.queosk.exception.CustomException;
import com.bttf.queosk.exception.ErrorCode;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;


@Transactional
@ExtendWith(MockitoExtension.class)
@DisplayName("테이블 관련 테스트코드")
class TableServiceTest {

    @Spy
    @InjectMocks
    private TableService tableService;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private TableRepository tableRepository;

    @Test
    @DisplayName("테이블 생성 (성공)")
    public void testCreateTable_success() {
        // Given
        Long restaurantId = 1L;
        Restaurant restaurant = Restaurant.builder()
                .id(restaurantId)
                .build();

        // When
        TableRequestForm form = new TableRequestForm("1번");
        tableService.createTable(restaurantId, form);

        // Then
        then(tableService).should(times(1)).createTable(restaurantId, form);
    }

    @Test
    @DisplayName("테이블 상태 수정 (성공)")
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
        tableService.updateTableStatus(table.getId(), TableStatus.USING, restaurantId);

        //then
        then(tableService).should(times(1)).updateTableStatus(table.getId(), TableStatus.USING, restaurantId);
    }

    @Test
    @DisplayName("테이블 상태 수정 (실패-table 찾을 수 없을 경우)")
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
    @DisplayName("테이블 상태 수정 (실패-본인 restaurant 의 table 이 아닌 경우)")
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
    @DisplayName("테이블 이름 수정 (성공)")
    public void testTableNameUpdate_success() {
        Long restaurantId = 1L;
        //given
        Table table = Table.builder()
                .id(1L)
                .status(TableStatus.OPEN)
                .restaurantId(1L)
                .build();

        TableNameUpdateForm form = TableNameUpdateForm.builder()
                .name("test")
                .build();

        given(tableRepository.findById(table.getId())).willReturn(Optional.of(table));

        //when
        tableService.updateTableName(table.getId(), form, restaurantId);

        //then
        then(tableService).should(times(1)).updateTableName(table.getId(), form, restaurantId);
    }

    @Test
    @DisplayName("테이블 이름 수정 (실패-table 찾을 수 없을 경우)")
    public void testUpdateTableName_fail_invalidTableException() {
        //given
        Table table = Table.builder()
                .id(1L)
                .status(TableStatus.OPEN)
                .restaurantId(1L)
                .build();

        TableNameUpdateForm form = TableNameUpdateForm.builder()
                .name("test")
                .build();

        given(tableRepository.findById(anyLong())).willReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> tableService.updateTableName(table.getId(), form, table.getRestaurantId()))
                .isExactlyInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_TABLE.getMessage());
        then(tableService).should(times(1)).updateTableName(table.getId(), form, table.getRestaurantId());
    }

    @Test
    @DisplayName("테이블 이름 수정 (실패-본인 restaurant 의 table 이 아닌 경우)")
    public void testUpdateTableName_fail_NotPermittedTableException() {
        //given
        Table table = Table.builder()
                .id(1L)
                .status(TableStatus.OPEN)
                .restaurantId(1L)
                .build();

        TableNameUpdateForm form = TableNameUpdateForm.builder()
                .name("test")
                .build();

        given(tableRepository.findById(table.getId())).willReturn(Optional.of(table));

        //then
        assertThatThrownBy(() -> tableService.updateTableName(table.getId(), form, 2L))
                .isExactlyInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.NOT_PERMITTED.getMessage());
        then(tableService).should(times(1)).updateTableName(table.getId(), form, 2L);
    }

    @Test
    @DisplayName("테이블 삭제 (성공)")
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
    @DisplayName("테이블 삭제 (실패-table 찾을 수 없을 경우")
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
    @DisplayName("테이블 가져오기 (성공)")
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
        TableDto table1 = tableService.getTable(table.getId(), restaurantId);

        // Then
        then(tableService).should(times(1)).getTable(table.getId(), restaurantId);
        assertThat(table1.getTableId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("테이블 가져오기 (실패-본인 restaurant 의 table 이 아닌 경우)")
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
        TableDto table1 = tableService.getTable(table.getId(), restaurantId);

        assertThatThrownBy(() -> tableService.getTable(table.getId(), 2L))
                .isExactlyInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.NOT_PERMITTED.getMessage());
        then(tableService).should(times(1)).getTable(table.getId(), 2L);
    }

    @Test
    @DisplayName("가게의 테이블 가져오기 (성공)")
    public void testGetTableList_success() {
        Long tableId = 1L;
        Long restaurantId = 1L;
        List<Table> tableList = new ArrayList<>();
        Table table1 = Table.builder()
                .id(1L)
                .status(TableStatus.OPEN)
                .restaurantId(restaurantId)
                .build();
        Table table2 = Table.builder()
                .id(2L)
                .status(TableStatus.OPEN)
                .restaurantId(restaurantId)
                .build();

        tableList.add(table1);
        tableList.add(table2);


        given(tableRepository.findByRestaurantId(restaurantId)).willReturn(tableList);

        // When
        List<TableDto> tableList1 = tableService.getTableList(restaurantId);

        then(tableService).should(times(1)).getTableList(restaurantId);
        assertThat(tableList.get(0).getId()).isEqualTo(tableList1.get(0).getTableId());
        assertThat(tableList.get(1).getId()).isEqualTo(tableList1.get(1).getTableId());
    }
}