package com.bttf.queosk.controller;

import com.bttf.queosk.dto.menuDto.CreateMenuForm;
import com.bttf.queosk.service.menuService.MenuService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Api(tags = "Menu API", description = "메뉴 API")
@RequestMapping("/api/restaurant")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @PostMapping("/{restaurantId}/menu")
    public ResponseEntity createMenu(@Valid @RequestBody CreateMenuForm createMenuForm, @PathVariable(name = "restaurantId") Long restaurantId) {
        menuService.createMenu(createMenuForm);
        return ResponseEntity.status(201).build();
    }


}
