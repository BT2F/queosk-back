package com.bttf.queosk.controller;

import com.bttf.queosk.dto.*;
import com.bttf.queosk.service.RefreshTokenService;
import com.bttf.queosk.service.RestaurantService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@Api(tags = "Restaurant API", description = "매장 API")
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/signup")
    @ApiOperation(value = "매장 회원가입", notes = "주어진 정보로 매장 회원가입을 진행합니다.")
    public ResponseEntity<?> signUp(@Valid @RequestBody RestaurantSignUpForm restaurantSignUpForm) throws Exception {
        restaurantService.signUp(restaurantSignUpForm);

        return ResponseEntity.status(201).build();
    }

    @PostMapping("/signin")
    @ApiOperation(value = "매장 로그인", notes = "주어진 정보로 매장 로그인을 진행합니다.")
    public ResponseEntity<?> signIn(@Valid @RequestBody RestaurantSignInForm restaurantSignInForm) {
        return ResponseEntity.ok().body(restaurantService.signIn(restaurantSignInForm));
    }

    @PostMapping("/image")
    @ApiOperation(value = "매장 이미지 추가", notes = "매장의 이미지를 추가합니다.")
    public ResponseEntity<?> restaurantImageUpload(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestBody MultipartFile image) throws IOException {

        restaurantService.imageUpload(token, image);
        return ResponseEntity.status(201).build();
    }

    @GetMapping
    @ApiOperation(value = "매장 정보 확인", notes = "매장의 정보를 확인합니다.")
    public ResponseEntity<?> restaurantGetInfo(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        RestaurantDto restaurant = restaurantService.getRestaurantInfoFromToken(token);
        return ResponseEntity.ok().body(restaurant);
    }

    @PutMapping("/resetpassword")
    @ApiOperation(value = "매장 비밀번호 초기화", notes = "매장의 비밀번호를 초기화 합니다.")
    public ResponseEntity<?> resetRestaurantPassword(@Valid @RequestBody
                                                     RestaurantResetPasswordForm restaurantResetPasswordForm) {
        restaurantService.resetRestaurantPassword(restaurantResetPasswordForm.getEmail(), restaurantResetPasswordForm.getOwnerName());
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @PutMapping("/updatepassword/{id}")
    @ApiOperation(value = "매장 비밀번호 변경", notes = "매장의 비밀번호를 변경합니다.")
    ResponseEntity<?> updateRestaurantPassword(@PathVariable(name = "id") Long id, @RequestBody RestaurantUpdatePasswordForm updatePassword) {
        restaurantService.updateRestaurantPassword(id, updatePassword);
        return ResponseEntity.status(CREATED).build();
    }

    @PostMapping("/signout")
    @ApiOperation(value = "매장 로그 아웃", notes = "매장 계정을 로그아웃 합니다.")
    public ResponseEntity<?> restaurantSignOut(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        RestaurantDto restaurant = restaurantService.getRestaurantInfoFromToken(token);
        refreshTokenService.deleteRefreshToken(restaurant.getEmail());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping
    @ApiOperation(value = "사업자 탈퇴", notes = "매장 계정을 삭제합니다.")
    public ResponseEntity<?> deleteRestaurant(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        restaurantService.deleteRestaurant(token);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @PostMapping
    @ApiOperation(value = "매장 수정", notes = "매장 계정의 정보를 수정합니다.")
    public ResponseEntity<?> updateRestaurantInfo(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                                  UpdateRestaurantInfoForm updateRestaurantInfoForm) {
        RestaurantDto restaurant = restaurantService.updateRestaurantInfo(token, updateRestaurantInfoForm);
        return ResponseEntity.status(201).body(restaurant);
    }

    @GetMapping("/coord")
    @ApiOperation(value = "동네 매장 검색 (좌표)", notes = "해당 좌표가 위치한 동네의 매장 리스트를 제공합니다.")
    public ResponseEntity<?> getCoordRestaurantInfo(@RequestBody RestaurantInfoGetCoordForm restaurantInfoGetCoordForm) {
        Page<RestaurantDto> restaurantPage = restaurantService.getCoordRestaurantInfoForm(restaurantInfoGetCoordForm);
        return ResponseEntity.ok().body(restaurantPage);
    }

    @GetMapping("/{restaurantId}")
    @ApiOperation(value = "매장 상세 보기", notes = "해당하는 매장의 정보와 메뉴를 제공합니댜.")
    public ResponseEntity<?> getRestaurantInfoAndMenu(@PathVariable(name = "restaurantId") Long restaurantId) {
        RestaurantInfoMenuGetDto restaurantInfoMenu = restaurantService.getRestaurantInfoAndMenu(restaurantId);

        return ResponseEntity.ok().body(restaurantInfoMenu);
    }
}
