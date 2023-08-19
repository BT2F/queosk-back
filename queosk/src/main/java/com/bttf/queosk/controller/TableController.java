package com.bttf.queosk.controller;

import com.bttf.queosk.dto.tabledto.TableForm;
import com.bttf.queosk.dto.userdto.UserDto;
import com.bttf.queosk.service.userservice.UserService;
import com.bttf.queosk.enumerate.TableStatus;
import com.bttf.queosk.service.TableService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurant")
@RequiredArgsConstructor
public class TableController {
    private final TableService tableService;
    private final UserService userService;

    @PostMapping("/table")
    @ApiOperation(value = "가게 테이블 생성", notes = "가게의 테이블을 추가합니다.")
    public ResponseEntity<?> tableCreate(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        UserDto userFromToken = userService.getUserFromToken(token);

        tableService.createTable(userFromToken.getId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/table/{tableId}")
    @ApiOperation(value = "테이블 상태 변경", notes = "테이블의 상태를 변경 합니다.")
    public ResponseEntity<?> tableUpdate(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                         @RequestParam TableStatus tableStatus,
                                         @PathVariable Long tableId) {

        UserDto userFromToken = userService.getUserFromToken(token);
        tableService.updateTable(tableId, tableStatus, userFromToken.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/table/{tableId}")
    @ApiOperation(value = "테이블 삭제", notes = "테이블을 삭제 합니다.")
    public ResponseEntity<?> tableDelete(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                         @PathVariable Long tableId) {

        UserDto userFromToken = userService.getUserFromToken(token);
        tableService.deleteTable(tableId, userFromToken.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/table/{tableId}")
    @ApiOperation(value = "테이블 조회", notes = "테이블을 조회 합니다.")
    public ResponseEntity<TableForm> tableGet(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                              @PathVariable Long tableId) {

        UserDto userDto = userService.getUserFromToken(token);
        return ResponseEntity.status(HttpStatus.OK).body(tableService.getTable(tableId, userDto.getId()));
    }

    @GetMapping("/tables")
    @ApiOperation(value = "테이블 조회", notes = "테이블을 조회 합니다.")
    public ResponseEntity<List<TableForm>> tableListGet(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        UserDto userDto = userService.getUserFromToken(token);
        return ResponseEntity.status(HttpStatus.OK).body(tableService.getTableList(userDto.getId()));
    }



}
