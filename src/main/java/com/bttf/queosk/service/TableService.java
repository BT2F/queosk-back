package com.bttf.queosk.service;


import com.bttf.queosk.dto.TableDto;
import com.bttf.queosk.dto.TableRequestForm;
import com.bttf.queosk.dto.TableUpdateForm;
import com.bttf.queosk.entity.Table;
import com.bttf.queosk.enumerate.TableStatus;
import com.bttf.queosk.exception.CustomException;
import com.bttf.queosk.repository.TableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.bttf.queosk.exception.ErrorCode.INVALID_TABLE;
import static com.bttf.queosk.exception.ErrorCode.NOT_PERMITTED;

@Service
@RequiredArgsConstructor
public class TableService {

    private final TableRepository tableRepository;

    @Transactional
    @CacheEvict(value = "tableList", key = "'restaurantId:' + #restaurantId")
    public void createTable(Long restaurantId, TableRequestForm form) {

        tableRepository.save(Table.of(restaurantId, form));
    }

    @Transactional
    @CacheEvict(value = "tableList", key = "'restaurantId:' + #restaurantId")
    public void updateTable(Long tableId, TableUpdateForm form, Long restaurantId) {

        Table table = getTableFromRepository(tableId);

        if (!restaurantId.equals(table.getRestaurantId())) {
            throw new CustomException(NOT_PERMITTED);
        }

        table.setStatusAndName(form.getTableStatus(), form.getName());
    }

    @Transactional
    @CacheEvict(value = "tableList", key = "'restaurantId:' + #restaurantId")
    public void deleteTable(Long tableId, Long restaurantId) {

        Table table = getTableFromRepository(tableId);

        if (!restaurantId.equals(table.getRestaurantId())) {
            throw new CustomException(NOT_PERMITTED);
        }

        tableRepository.delete(table);
    }

    @Transactional(readOnly = true)
    public TableDto getTable(Long tableId, Long restaurantId) {

        Table table = getTableFromRepository(tableId);

        if (!restaurantId.equals(table.getRestaurantId())) {
            throw new CustomException(NOT_PERMITTED);
        }

        return TableDto.of(table);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "tableList", key = "'restaurantId:' + #restaurantId")
    public List<TableDto> getTableList(Long restaurantId) {

        return tableRepository.findByRestaurantId(restaurantId)
                .stream()
                .map(TableDto::of)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public List<TableDto> getAvailableTables(Long restaurantId) {
        return tableRepository.findByRestaurantId(restaurantId)
                .stream()
                .filter(table -> table.getStatus() == TableStatus.OPEN)
                .map(TableDto::of)
                .collect(Collectors.toList());
    }

    private Table getTableFromRepository(Long tableId) {
        return tableRepository.findById(tableId).orElseThrow(
                () -> new CustomException(INVALID_TABLE)
        );
    }


}
