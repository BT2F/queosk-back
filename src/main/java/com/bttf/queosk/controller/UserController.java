package com.bttf.queosk.controller;

import com.bttf.queosk.config.JwtTokenProvider;
import com.bttf.queosk.dto.*;
import com.bttf.queosk.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.UUID;

import static com.bttf.queosk.enumerate.LoginType.KAKAO;
import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@Api(tags = "User API", description = "사용자와 관련된 API")
@RequestMapping("/api/users")
@RestController
public class UserController {
    private final UserLoginService userLoginService;
    private final UserInfoService userInfoService;
    private final RefreshTokenService refreshTokenService;
    private final ImageService imageService;
    private final KakaoLoginService kakaoLoginService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signup")
    @ApiOperation(value = "사용자 회원가입", notes = "입력된 정보로 회원가입을 진행합니다.")
    public ResponseEntity<Void> createUser(
            @Valid @RequestBody UserSignUpRequestForm userSignUpRequestForm) {

        userLoginService.createUser(userSignUpRequestForm);

        return ResponseEntity.status(CREATED).build();
    }

    @PostMapping("/signin")
    @ApiOperation(value = "사용자 로그인", notes = "입력된 정보로 로그인을 진행합니다.")
    public ResponseEntity<UserSignInResponseForm> signIn(
            @Valid @RequestBody UserSignInRequestForm userSignInRequestForm) {

        UserSignInDto userSignInDto = userLoginService.signInUser(userSignInRequestForm);

        return ResponseEntity.status(OK).body(UserSignInResponseForm.of(userSignInDto));
    }

    @PostMapping("/check")
    @ApiOperation(value = "이메일 중복 확인", notes = "사용하고자 하는 이메일의 중복여부를 확인합니다.")
    public ResponseEntity<String> checkDuplication(
            @Valid @RequestBody UserCheckRequestForm userCheckRequestForm) {

        return userLoginService.checkDuplication(userCheckRequestForm.getEmail()) ?
                ResponseEntity.status(OK).body("사용가능한 이메일 입니다.") :
                ResponseEntity.status(BAD_REQUEST).body("이미 사용중인 이메일 입니다.");
    }

    @GetMapping
    @ApiOperation(value = "사용자 본인 상세정보", notes = "사용자 본인의 상세정보를 조회합니다.")
    public ResponseEntity<UserDetailsResponseForm> getUserDetails(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        UserDto userDto = userLoginService.getUserFromToken(token);

        return ResponseEntity.status(OK).body(UserDetailsResponseForm.of(userDto));
    }

    @GetMapping("/{userId}")
    @ApiOperation(value = "다른 사용자 상세정보", notes = "다른 사용자의 상세정보를 조회합니다.(정보제한 필요)")
    public ResponseEntity<UserDetailsResponseForm> getOtherUserDetails(
            @PathVariable Long userId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        UserDto userDto = userInfoService.getUserDetails(userId);

        return ResponseEntity.status(OK).body(UserDetailsResponseForm.of(userDto));
    }

    @PutMapping
    @ApiOperation(value = "사용자 정보 수정", notes = "사용자의 상세정보를 수정합니다.")
    public ResponseEntity<UserEditResponseForm> editUserDetails(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Valid @RequestBody UserEditRequestForm userEditRequestForm) {

        Long userId = jwtTokenProvider.getIdFromToken(token);

        UserDto userDto = userInfoService.editUserInformation(userId, userEditRequestForm);

        return ResponseEntity.status(OK).body(UserEditResponseForm.of(userDto));
    }

    @PutMapping("/password/change")
    @ApiOperation(value = "사용자 비밀번호 변경", notes = "사용자의 비밀번호를 변경합니다.")
    public ResponseEntity<Void> changeUserPassword(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Valid @RequestBody UserPasswordChangeRequestForm userPasswordChangeRequestForm) {

        Long userId = jwtTokenProvider.getIdFromToken(token);

        userInfoService.changeUserPassword(userId, userPasswordChangeRequestForm);

        return ResponseEntity.status(NO_CONTENT).build();
    }

    @PutMapping("/password/reset")
    @ApiOperation(value = "사용자 비밀번호 초기화", notes = "사용자의 비밀번호를 초기화 후 이메일로 새 비밀번호를 전송 합니다.")
    public ResponseEntity<Void> resetUserPassword(
            @Valid @RequestBody UserPasswordResetRequestForm userPasswordResetRequestForm) {

        userInfoService.resetUserPassword(userPasswordResetRequestForm.getEmail(),
                userPasswordResetRequestForm.getNickName());

        return ResponseEntity.status(NO_CONTENT).build();
    }

    @PostMapping("/signout")
    @ApiOperation(value = "사용자 로그아웃", notes = "사용자의 계정에서 로그아웃합니다.")
    public ResponseEntity<Void> signOutUser(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        UserDto userDto = userLoginService.getUserFromToken(token);

        if (userDto.getLoginType().equals(KAKAO.toString())) {
            kakaoLoginService.logoutKakaoUser(userDto.getEmail());
        }

        refreshTokenService.deleteRefreshToken(userDto.getEmail());

        return ResponseEntity.status(NO_CONTENT).build();
    }

    @PostMapping("/signup/image")
    @ApiOperation(value = "프로필 업로드 및 경로 가져오기(회원가입 이전)",
            notes = "사용자의 새로운 프로필 사진을 이미지서버에 업로드 하고 imageUrl을 가져옵니다.")
    public ResponseEntity<ImageUrlResponseForm> uploadImageAndGetUrl(
            @RequestBody MultipartFile image) throws IOException {

        String url = imageService.saveFile(image, "user");

        return ResponseEntity.status(CREATED).body(ImageUrlResponseForm.of(url));
    }

    @PutMapping("/image")
    @ApiOperation(value = "프로필 이미지 수정",
            notes = "사용자의 새로운 프로필 사진을 이미지서버에 업로드 하고 사용자 imageUrl을 교체합니다.")
    public ResponseEntity<Void> uploadImage(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestBody MultipartFile image) throws IOException {

        Long userId = jwtTokenProvider.getIdFromToken(token);

        UserDto userDto = userInfoService.getUserDetails(userId);

        // 기존 이미지 삭제
        imageService.deleteFile(userDto.getImageUrl());

        // 새로운 이미지경로 업데이트
        userInfoService.updateImageUrl(userId, imageService.saveFile(image, "user"));

        return ResponseEntity.status(NO_CONTENT).build();
    }

    @PutMapping("/withdrawal")
    @ApiOperation(value = "사용자 회원탈퇴", notes = "사용자 상태를 '삭제' 상태로 변경합니다.")
    public ResponseEntity<Void> withdrawUser(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        Long userId = jwtTokenProvider.getIdFromToken(token);

        userInfoService.withdrawUser(userId);

        return ResponseEntity.status(NO_CONTENT).build();
    }
}