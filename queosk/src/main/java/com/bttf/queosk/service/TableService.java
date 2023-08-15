package com.bttf.queosk.service;


import com.bttf.queosk.entity.TableEntity;
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
                () -> new RuntimeException()
        );

        tableRepository.save(TableEntity.of(restaurantId));
    }
}
