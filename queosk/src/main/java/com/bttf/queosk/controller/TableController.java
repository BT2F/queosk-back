package com.bttf.queosk.controller;

import com.bttf.queosk.dto.userDto.UserDto;
import com.bttf.queosk.service.TableService;
import com.bttf.queosk.service.userService.UserService;
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
}
