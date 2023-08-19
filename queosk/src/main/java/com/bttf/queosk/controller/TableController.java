package com.bttf.queosk.controller;

import com.bttf.queosk.enumerate.TableStatus;
import com.bttf.queosk.service.TableService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/restaurant/{restaurantId}")
@RequiredArgsConstructor
public class TableController {
    private final TableService tableService;

    @PostMapping("/table")
    @ApiOperation(value = "가게 테이블 생성", notes = "가게의 테이블을 추가합니다.")
    public ResponseEntity<?> tableCreate(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                         @PathVariable(name = "restaurantId") Long restaurantId) {
        tableService.createTable(restaurantId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/table/{tableId}")
    @ApiOperation(value = "테이블 상태 변경", notes = "테이블의 상태를 변경 합니다.")
    public ResponseEntity<?> tableUpdate(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                         @PathVariable(name = "restaurantId") Long restaurantId,
                                         @RequestParam TableStatus tableStatus,
                                         @PathVariable Long tableId) {

        tableService.updateTable(tableId, tableStatus);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/table/{tableId}")
    @ApiOperation(value = "테이블 삭제", notes = "테이블을 삭제 합니다.")
    public ResponseEntity<?> tableDelete(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                         @PathVariable(name = "restaurantId") Long restaurantId,
                                         @PathVariable Long tableId) {

        tableService.deleteTable(tableId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }



}
