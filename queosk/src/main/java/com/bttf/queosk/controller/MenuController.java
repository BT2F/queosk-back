package com.bttf.queosk.controller;

import com.bttf.queosk.config.springsecurity.JwtTokenProvider;
import com.bttf.queosk.dto.menudto.CreateMenuForm;
import com.bttf.queosk.service.menuservice.MenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Api(tags = "Menu API", description = "메뉴 관련 API")
@RequestMapping("/api/restaurant")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/menu")
    @ApiOperation(value = "식당 메뉴 목록 추가", notes = "점주가 본인 식당의 메뉴목록을 추가합니다.")
    public ResponseEntity<?> createMenu(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Valid @RequestBody CreateMenuForm createMenuForm) {

        Long restaurantId = jwtTokenProvider.getIdFromToken(token);

        menuService.createMenu(restaurantId,createMenuForm);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
