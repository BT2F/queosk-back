package com.bttf.queosk.controller;

import com.bttf.queosk.config.springsecurity.JwtTokenProvider;
import com.bttf.queosk.dto.menudto.MenuCreationForm;
import com.bttf.queosk.dto.menudto.MenuDto;
import com.bttf.queosk.dto.menudto.MenuUpdateForm;
import com.bttf.queosk.service.menuservice.MenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Api(tags = "Menu API", description = "메뉴 관련 API")
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/menus")
    @ApiOperation(value = "식당 메뉴 목록 추가", notes = "점주가 본인 식당의 메뉴목록을 추가합니다.")
    public ResponseEntity<?> createMenu(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Valid @RequestBody MenuCreationForm menuCreationForm) {

        Long restaurantId = jwtTokenProvider.getIdFromToken(token);

        menuService.createMenu(restaurantId,menuCreationForm);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @GetMapping("/{restaurantId}/menus")
    @ApiOperation(value = "식당 메뉴 목록 조회", notes = "점주 또는 고객이 대상 식당의 메뉴목록을 조회합니다.")
    public ResponseEntity<?> getMenu(
            @PathVariable Long restaurantId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token){

        List<MenuDto> menuList = menuService.getMenu(restaurantId);

        return ResponseEntity.status(HttpStatus.OK).body(menuList);
    }

    @PutMapping("/menus/{menuId}")
    @ApiOperation(value = "식당 메뉴정보 수정", notes = "점주가 본인 식당의 메뉴정보를 수정합니다.")
    public ResponseEntity<?> updateMenuInfo(
            @PathVariable Long menuId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestBody MenuUpdateForm menuUpdateForm){

        Long restaurantId = jwtTokenProvider.getIdFromToken(token);

        menuService.updateMenuInfo(restaurantId,menuId,menuUpdateForm);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
