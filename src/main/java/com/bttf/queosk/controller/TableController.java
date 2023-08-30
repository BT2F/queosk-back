package com.bttf.queosk.controller;

import com.bttf.queosk.config.JwtTokenProvider;
import com.bttf.queosk.dto.tabledto.TableDto;
import com.bttf.queosk.dto.tabledto.TableForm;
import com.bttf.queosk.enumerate.TableStatus;
import com.bttf.queosk.mapper.TableMapper;
import com.bttf.queosk.service.TableService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/restaurant")
@RequiredArgsConstructor
public class TableController {
    private final TableService tableService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/table")
    @ApiOperation(value = "가게 테이블 생성", notes = "가게의 테이블을 추가합니다.")
    public ResponseEntity<?> tableCreate(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        Long restaurantId = jwtTokenProvider.getIdFromToken(token);

        tableService.createTable(restaurantId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/table/{tableId}")
    @ApiOperation(value = "테이블 상태 변경", notes = "테이블의 상태를 변경 합니다.")
    public ResponseEntity<?> tableUpdate(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                         @RequestParam TableStatus tableStatus,
                                         @PathVariable Long tableId) {

        Long restaurantId = jwtTokenProvider.getIdFromToken(token);
        tableService.updateTable(tableId, tableStatus, restaurantId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/table/{tableId}")
    @ApiOperation(value = "테이블 삭제", notes = "테이블을 삭제 합니다.")
    public ResponseEntity<?> tableDelete(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                         @PathVariable Long tableId) {

        Long restaurantId = jwtTokenProvider.getIdFromToken(token);

        tableService.deleteTable(tableId, restaurantId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/table/{tableId}")
    @ApiOperation(value = "테이블 조회", notes = "테이블을 조회 합니다.")
    public ResponseEntity<?> tableGet(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                      @PathVariable Long tableId) {

        Long restaurantId = jwtTokenProvider.getIdFromToken(token);

        TableDto tableDto = tableService.getTable(tableId, restaurantId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(TableMapper.INSTANCE.tableDtoToTableForm(tableDto));
    }

    @GetMapping("/tables")
    @ApiOperation(value = "테이블 조회", notes = "테이블을 조회 합니다.")
    public ResponseEntity<?> tableListGet(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        Long restaurantId = jwtTokenProvider.getIdFromToken(token);

        List<TableDto> tableList = tableService.getTableList(restaurantId);

        List<TableForm> tableFormList = new ArrayList<>();

        for (TableDto dto : tableList) {
            tableFormList.add(TableMapper.INSTANCE.tableDtoToTableForm(dto));
        }
        return ResponseEntity.status(HttpStatus.OK).body(tableFormList);
    }
}
