package com.bttf.queosk.service;


import com.bttf.queosk.enumerate.TableStatus;
import com.bttf.queosk.entity.Table;
import com.bttf.queosk.exception.CustomException;
import com.bttf.queosk.exception.ErrorCode;
import com.bttf.queosk.repository.RestaurantRepository;
import com.bttf.queosk.repository.TableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TableService {

    private final TableRepository tableRepository;

    private final RestaurantRepository restaurantRepository;

    public void createTable(Long restaurantId) {

        restaurantRepository.findById(restaurantId).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_RESTAURANT)
        );

        tableRepository.save(Table.of(restaurantId));
    }

    public void updateTable(Long tableId, TableStatus tableStatus) {

        Table table = tableRepository.findById(tableId).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_TABLE)
        );

        Table.updateStatus(table, tableStatus);
    }

    public void deleteTable(Long tableId) {

        Table table = tableRepository.findById(tableId).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_TABLE)
        );

        tableRepository.delete(table);
    }
}
