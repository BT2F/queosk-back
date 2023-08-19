package com.bttf.queosk.controller;

import com.bttf.queosk.dto.restaurantdto.*;
import com.bttf.queosk.service.refreshtokenservice.RefreshTokenService;
import com.bttf.queosk.service.restaurantservice.RestaurantService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/restaurant")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final RefreshTokenService refreshTokenService;

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

    @PostMapping("/image")
    @ApiOperation(value = "이미지 추가", notes = "업장의 이미지를 추가합니다.")
    public ResponseEntity<?> restaurantImageUpload(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestBody MultipartFile image) throws IOException {

        restaurantService.imageUpload(token, image);
        return ResponseEntity.status(201).build();
    }

    @GetMapping
    @ApiOperation(value = "매장 정보 확인", notes = "업장의 정보를 확인합니다.")
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
    ResponseEntity<?> updateRestaurantPassword(@PathVariable(name = "id") Long id, @RequestBody RestaurantUpdatePasswordForm updatePassword) {
        restaurantService.updateRestaurantPassword(id, updatePassword);
        return ResponseEntity.status(CREATED).build();
    }

    @PostMapping("/signout")
    @ApiOperation(value = "매장 로그 아웃", notes = "업장 계정을 로그아웃 합니다.")
    public ResponseEntity<?> restaurantSignOut(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        RestaurantDto restaurant = restaurantService.getRestaurantInfoFromToken(token);
        refreshTokenService.deleteRefreshToken(restaurant.getEmail());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping
    @ApiOperation(value = "사업자 탈퇴", notes = "업장 계정을 삭제합니다.")
    public ResponseEntity<?> deleteRestaurant(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        restaurantService.deleteRestaurant(token);
        return ResponseEntity.status(NO_CONTENT).build();
    }
  
    @PostMapping
    @ApiOperation(value = "매장 조회", notes = "업장 계정의 정보를 수정합니다.")
    public ResponseEntity<?> updateRestaurantInfo(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                                  UpdateRestorantInfoForm updateRestorantInfoForm) {
        RestaurantDto restaurant = restaurantService.updateRestaurantInfo(token, updateRestorantInfoForm);
        return ResponseEntity.status(201).body(restaurant);
    }
}
