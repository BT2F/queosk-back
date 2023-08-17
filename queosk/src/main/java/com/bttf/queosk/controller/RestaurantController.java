package com.bttf.queosk.controller;

import com.bttf.queosk.dto.restaurantDto.RestaurantSignUpForm;
import com.bttf.queosk.dto.restaurantDto.RestaurantSignInForm;
import com.bttf.queosk.service.RestaurantService.RestaurantService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@Api(tags = "Restaurant API", description = "매장 API")
@RequestMapping("/api/restaurant")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping("/signup")
    @ApiOperation(value = "사업자 회원가입", notes = "주어진 정보로 사업자 회원가입을 진행합니다.")
    public ResponseEntity<?> signUp(@Valid @RequestBody RestaurantSignUpForm restaurantSignUpForm) throws Exception {
        restaurantService.signUp(restaurantSignUpForm);

        return ResponseEntity.status(201).build();
    }

    @PostMapping("/signin")
    @ApiOperation(value = "사업자 로그인", notes = "주어진 정보로 사업자 로그인을 진행합니다.")
    public ResponseEntity<?> signIn(@Valid @RequestBody RestaurantSignInForm restaurantSignInForm) {
        return ResponseEntity.ok().body(restaurantService.signIn(restaurantSignInForm));
    }

    @PostMapping("/image/{id}")
    @ApiOperation(value = "이미지 추가", notes = "업장의 이미지를 추가합니다.")
    public ResponseEntity<?> restaurantImageUpload(@PathVariable(name = "id") long id, @RequestBody MultipartFile image) throws IOException {
        restaurantService.imageUpload(id, image);
        return ResponseEntity.status(201).build();
    }

}
