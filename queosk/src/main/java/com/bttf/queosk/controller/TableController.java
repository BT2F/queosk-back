package com.bttf.queosk.controller;

import com.bttf.queosk.dto.tableDto.TableForm;
import com.bttf.queosk.mapper.TableMapper;
import com.bttf.queosk.service.TableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/restaurant/{restaurantId}")
@RequiredArgsConstructor
public class TableController {
    private final TableService tableService;

    public ResponseEntity<?> tableCreate(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                      @PathVariable(name = "restaurantId") Long restaurantId) {
        tableService.createTable(restaurantId);
        return ResponseEntity.status(201).build();
    }
}
