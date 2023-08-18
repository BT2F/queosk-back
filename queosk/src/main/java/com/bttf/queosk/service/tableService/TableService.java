package com.bttf.queosk.service.tableService;


import com.bttf.queosk.dto.enumerate.TableStatus;
import com.bttf.queosk.dto.tableDto.TableForm;
import com.bttf.queosk.entity.Table;
import com.bttf.queosk.exception.CustomException;
import com.bttf.queosk.mapper.tableMapper.TableMapper;
import com.bttf.queosk.repository.RestaurantRepository;
import com.bttf.queosk.repository.TableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

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

        tableRepository.save(
                TableMapper.INSTANCE.updateStatusWithDto(
                        tableStatus, TableMapper.INSTANCE.tableToTableDto(table)
                )
        );
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
                TableMapper.INSTANCE.tableToTableDto(table));
    }

    private Table getTableFromRepository(Long tableId) {
        return tableRepository.findById(tableId).orElseThrow(
                () -> new CustomException(INVALID_TABLE)
        );
    }
}
