package com.bttf.queosk.service.tableservice;


import com.bttf.queosk.enumerate.TableStatus;
import com.bttf.queosk.dto.tableDto.TableDto;
import com.bttf.queosk.dto.tableDto.TableForm;
import com.bttf.queosk.entity.Table;
import com.bttf.queosk.exception.CustomException;
import com.bttf.queosk.mapper.tablemapper.TableMapper;
import com.bttf.queosk.repository.RestaurantRepository;
import com.bttf.queosk.repository.TableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.bttf.queosk.exception.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class TableService {

    private final TableRepository tableRepository;

    private final RestaurantRepository restaurantRepository;

    public void createTable(Long restaurantId) {

        tableRepository.save(TableMapper.INSTANCE.restaurantToTable(
                        restaurantRepository.findById(restaurantId).orElseThrow(
                                () -> new CustomException(INVALID_RESTAURANT)
                        )
                )
        );
    }

    public void updateTable(Long tableId, TableStatus tableStatus, Long restaurantId) {

        Table table = getTableFromRepository(tableId);

        if (!restaurantId.equals(table.getRestaurantId())) {
            throw new CustomException(NOT_PERMITTED);
        }

        Table.updateStatus(table, tableStatus);
    }

    public void deleteTable(Long tableId, Long restaurantId) {

        Table table = getTableFromRepository(tableId);

        if (!restaurantId.equals(table.getRestaurantId())) {
            throw new CustomException(NOT_PERMITTED);
        }

        tableRepository.delete(table);
    }

    public TableForm getTable(Long tableId, Long restaurantId) {

        Table table = getTableFromRepository(tableId);

        if (!restaurantId.equals(table.getRestaurantId())) {
            throw new CustomException(NOT_PERMITTED);
        }

        return TableMapper.INSTANCE.tableDtoToTableForm(
                TableDto.of(table));
    }

    public List<TableForm> getTableList(Long restaurantId) {

        List<TableDto> tableDtos = tableRepository.findByRestaurantId(restaurantId)
                .stream()
                .map(TableDto::of)
                .collect(Collectors.toList());

        List<TableForm> tableForms = new ArrayList<>();
        for (TableDto dto : tableDtos) {
            TableForm tableForm = TableMapper.INSTANCE.tableDtoToTableForm(dto);
            tableForms.add(tableForm);
        }

        return tableForms;
    }

    private Table getTableFromRepository(Long tableId) {
        return tableRepository.findById(tableId).orElseThrow(
                () -> new CustomException(INVALID_TABLE)
        );
    }


}
