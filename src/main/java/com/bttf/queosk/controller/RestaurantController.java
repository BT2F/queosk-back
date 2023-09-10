package com.bttf.queosk.controller;

import com.bttf.queosk.config.JwtTokenProvider;
import com.bttf.queosk.dto.*;
import com.bttf.queosk.enumerate.RestaurantCategory;
import com.bttf.queosk.service.RefreshTokenService;
import com.bttf.queosk.service.RestaurantService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

import static org.springframework.http.HttpStatus.*;

@RestController
@Api(tags = "Restaurant API", description = "매장 API")
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signup")
    @ApiOperation(value = "매장 회원가입", notes = "주어진 정보로 매장 회원가입을 진행합니다.")
    public ResponseEntity<Void> signUp(
            @Valid @RequestBody RestaurantSignUpForm.Request restaurantSignUpRequest) throws Exception {
        restaurantService.signUp(restaurantSignUpRequest);

        return ResponseEntity.status(CREATED).build();
    }

    @PostMapping("/signin")
    @ApiOperation(value = "매장 로그인", notes = "주어진 정보로 매장 로그인을 진행합니다.")
    public ResponseEntity<RestaurantSignInForm.Response> signIn(
            @Valid @RequestBody RestaurantSignInForm.Request restaurantSignInRequest) {
        return ResponseEntity.status(OK).body(RestaurantSignInForm.Response
                .of(restaurantService.signIn(restaurantSignInRequest)));
    }

    @PostMapping("/image")
    @ApiOperation(value = "매장 이미지 추가", notes = "매장의 이미지를 추가합니다.")
    public ResponseEntity<Void> restaurantImageUpload(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestBody MultipartFile image) throws IOException {

        restaurantService.imageUpload(token, image);
        return ResponseEntity.status(CREATED).build();
    }

    @GetMapping
    @ApiOperation(value = "매장 정보 확인", notes = "매장의 정보를 확인합니다.")
    public ResponseEntity<RestaurantGetInfoForm.Response> restaurantGetInfo(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        RestaurantDto restaurant = restaurantService.getRestaurantInfoFromToken(token);
        return ResponseEntity.status(OK).body(RestaurantGetInfoForm.Response.of(restaurant));
    }

    @PatchMapping("/password/reset")
    @ApiOperation(value = "매장 비밀번호 초기화", notes = "매장의 비밀번호를 초기화 합니다.")
    public ResponseEntity<Void> resetRestaurantPassword(
            @Valid @RequestBody RestaurantResetPasswordForm.Request restaurantResetPasswordRequest) {
        restaurantService.resetRestaurantPassword(restaurantResetPasswordRequest.getEmail(),
                restaurantResetPasswordRequest.getOwnerName());
        return ResponseEntity.status(CREATED).build();
    }

    @PatchMapping("/password/change")
    @ApiOperation(value = "매장 비밀번호 변경", notes = "매장의 비밀번호를 변경합니다.")
    ResponseEntity<Void> updateRestaurantPassword(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestBody RestaurantUpdatePasswordForm.Request updatePasswordRequest) {
        Long id = jwtTokenProvider.getIdFromToken(token);
        restaurantService.updateRestaurantPassword(id, updatePasswordRequest);
        return ResponseEntity.status(CREATED).build();
    }

    @PostMapping("/signout")
    @ApiOperation(value = "매장 로그 아웃", notes = "매장 계정을 로그아웃 합니다.")
    public ResponseEntity<Void> restaurantSignOut(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        RestaurantDto restaurant = restaurantService.getRestaurantInfoFromToken(token);
        refreshTokenService.deleteRefreshToken(restaurant.getEmail());
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @DeleteMapping
    @ApiOperation(value = "사업자 탈퇴", notes = "매장 계정을 삭제합니다.")
    public ResponseEntity<Void> deleteRestaurant(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        restaurantService.deleteRestaurant(token);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @PostMapping
    @ApiOperation(value = "매장 수정", notes = "매장 계정의 정보를 수정합니다.")
    public ResponseEntity<RestaurantGetInfoForm.Response> updateRestaurantInfo(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            UpdateRestaurantInfoForm.Request updateRestaurantInfoRequest) {
        RestaurantDto restaurantDto = restaurantService.updateRestaurantInfo(token, updateRestaurantInfoRequest);
        return ResponseEntity.status(CREATED).body(RestaurantGetInfoForm.Response.of(restaurantDto));
    }

    @GetMapping("/coord")
    @ApiOperation(value = "동네 매장 검색 (좌표)", notes = "해당 좌표가 위치한 동네의 매장 리스트를 제공합니다.")
    public ResponseEntity<Page<RestaurantInfoGetCoordForm.Response>> getCoordRestaurantInfo(
            @RequestParam(value = "x", defaultValue = "0") Double x,
            @RequestParam(value = "y", defaultValue = "0") Double y,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "category",  defaultValue = "ALL") String category) {

        RestaurantCategory restaurantCategory = RestaurantCategory.valueOf(category);
        Page<RestaurantDto> restaurantDtoPage = restaurantService
                .getCoordRestaurantInfoForm(x, y, page, size, restaurantCategory);
        Page<RestaurantInfoGetCoordForm.Response> responsePage =
                restaurantDtoPage.map(RestaurantInfoGetCoordForm.Response::of);
        return ResponseEntity.status(OK).body(responsePage);
    }

    @GetMapping("/{restaurantId}")
    @ApiOperation(value = "매장 상세 보기", notes = "해당하는 매장의 정보와 메뉴를 제공합니댜.")
    public ResponseEntity<RestaurantInfoMenuGetForm.Response> getRestaurantInfoAndMenu(
            @PathVariable(name = "restaurantId") Long restaurantId) {
        RestaurantInfoMenuGetDto restaurantInfoMenu = restaurantService
                .getRestaurantInfoAndMenu(restaurantId);
        return ResponseEntity.status(OK).body(RestaurantInfoMenuGetForm
                .Response.of(restaurantInfoMenu));
    }
}
