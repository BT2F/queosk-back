package com.bttf.queosk.controller;

import com.bttf.queosk.config.springsecurity.JwtTokenProvider;
import com.bttf.queosk.dto.UserImageUrlDto;
import com.bttf.queosk.dto.userdto.*;
import com.bttf.queosk.service.imageservice.ImageService;
import com.bttf.queosk.service.kakaoservice.KakaoLoginService;
import com.bttf.queosk.service.refreshtokenservice.RefreshTokenService;
import com.bttf.queosk.service.userservice.UserInfoService;
import com.bttf.queosk.service.userservice.UserLoginService;
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
import java.util.UUID;

import static com.bttf.queosk.enumerate.LoginType.KAKAO;

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
    public ResponseEntity<?> createUser(@Valid @RequestBody UserSignUpForm userSignUpForm) {

        userLoginService.createUser(userSignUpForm);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/signin")
    @ApiOperation(value = "사용자 로그인", notes = "입력된 정보로 로그인을 진행합니다.")
    public ResponseEntity<?> signIn(@Valid @RequestBody UserSignInForm userSignInForm) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userLoginService.signInUser(userSignInForm));
    }

    @PostMapping("/check")
    @ApiOperation(value = "이메일 중복 확인", notes = "사용하고자 하는 이메일의 중복여부를 확인합니다.")
    public ResponseEntity<?> checkDuplication(@Valid @RequestBody UserCheckForm userCheckForm) {

        return userLoginService.checkDuplication(userCheckForm.getEmail()) ?
                ResponseEntity.status(HttpStatus.OK).body("사용가능한 이메일 입니다.") :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 사용중인 이메일 입니다.");
    }

    @GetMapping
    @ApiOperation(value = "사용자 상세정보", notes = "사용자의 상세정보를 조회합니다.")
    public ResponseEntity<?> getUserDetails(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        UserDto user = userLoginService.getUserFromToken(token);

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PutMapping
    @ApiOperation(value = "사용자 정보 수정", notes = "사용자의 상세정보를 수정합니다.")
    public ResponseEntity<?> editUserDetails(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Valid @RequestBody UserEditForm userEditForm) {

        Long userId = jwtTokenProvider.getIdFromToken(token);

        UserDto result = userInfoService.editUserInformation(userId, userEditForm);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PutMapping("/password/change")
    @ApiOperation(value = "사용자 비밀번호 변경", notes = "사용자의 비밀번호를 변경합니다.")
    public ResponseEntity<?> changeUserPassword(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Valid @RequestBody UserPasswordChangeForm userPasswordChangeForm) {

        Long userId = jwtTokenProvider.getIdFromToken(token);

        userInfoService.changeUserPassword(userId, userPasswordChangeForm);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/password/reset")
    @ApiOperation(value = "사용자 비밀번호 초기화", notes = "사용자의 비밀번호를 초기화 후 이메일로 새 비밀번호를 전송 합니다.")
    public ResponseEntity<?> resetUserPassword(
            @Valid @RequestBody UserResetPasswordForm userResetPasswordForm) {

        userInfoService.resetUserPassword(userResetPasswordForm.getEmail(),
                userResetPasswordForm.getNickName());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/signout")
    @ApiOperation(value = "사용자 로그아웃", notes = "사용자의 계정에서 로그아웃합니다.")
    public ResponseEntity<?> signOutUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        UserDto userDto = userLoginService.getUserFromToken(token);

        if (userDto.getLoginType().equals(KAKAO.toString())) {
            kakaoLoginService.getKakaoLogout(userDto.getEmail());
        }

        refreshTokenService.deleteRefreshToken(userDto.getEmail());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/signup/image")
    @ApiOperation(value = "프로필 업로드 및 경로 가져오기(회원가입 이전)",
            notes = "사용자의 새로운 프로필 사진을 이미지서버에 업로드 하고 imageUrl을 가져옵니다.")
    public ResponseEntity<?> uploadImageAndGetUrl(
            @RequestBody MultipartFile image) throws IOException {

        String url = imageService.saveFile(image, "user/" + UUID.randomUUID()
                .toString().substring(0, 6));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserImageUrlDto.builder().imagePath(url));
    }

    @PutMapping("/image")
    @ApiOperation(value = "프로필 이미지 수정",
            notes = "사용자의 새로운 프로필 사진을 이미지서버에 업로드 하고 사용자 imageUrl을 교체합니다.")
    public ResponseEntity<?> uploadImage(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestBody MultipartFile image) throws IOException {

        Long userId = jwtTokenProvider.getIdFromToken(token);

        String url = imageService.saveFile(image, "user/" + userId);

        userInfoService.updateImageUrl(userId, url);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/withdrawal")
    @ApiOperation(value = "사용자 회원탈퇴", notes = "사용자 상태를 '삭제' 상태로 변경합니다.")
    public ResponseEntity<?> withdrawUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        Long userId = jwtTokenProvider.getIdFromToken(token);

        userInfoService.withdrawUser(userId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}