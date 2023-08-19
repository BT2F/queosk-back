package com.bttf.queosk.controller;

import com.bttf.queosk.dto.userdto.*;
import com.bttf.queosk.service.imageservice.ImageService;
import com.bttf.queosk.service.refreshtokenservice.RefreshTokenService;
import com.bttf.queosk.service.userservice.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

@RequiredArgsConstructor
@Api(tags = "User API", description = "사용자와 관련된 API")
@RequestMapping("/api/users")
@RestController
public class UserController {
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final ImageService imageService;

    @PostMapping("/signup")
    @ApiOperation(value = "사용자 회원가입", notes = "입력된 정보로 회원가입을 진행합니다.")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserSignUpForm userSignUpForm) {

        userService.createUser(userSignUpForm);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/signin")
    @ApiOperation(value = "사용자 로그인", notes = "입력된 정보로 로그인을 진행합니다.")
    public ResponseEntity<?> signIn(@Valid @RequestBody UserSignInForm userSignInForm) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.signInUser(userSignInForm));
    }

    @PostMapping("/check")
    @ApiOperation(value = "이메일 중복 확인", notes = "사용하고자 하는 이메일의 중복여부를 확인합니다.")
    public ResponseEntity<?> checkDuplication(@Valid @RequestBody UserCheckForm userCheckForm) {

        return userService.checkDuplication(userCheckForm.getEmail()) ?
                ResponseEntity.status(HttpStatus.OK).body("사용가능한 이메일 입니다.") :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 사용중인 이메일 입니다.");
    }

    @GetMapping
    @ApiOperation(value = "사용자 상세정보", notes = "사용자의 상세정보를 조회합니다.")
    public ResponseEntity<?> getUserDetails(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        UserDto user = userService.getUserFromToken(token);

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PutMapping
    @ApiOperation(value = "사용자 정보 수정", notes = "사용자의 상세정보를 수정합니다.")
    public ResponseEntity<?> editUserDetails(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Valid @RequestBody UserEditForm userEditForm) {

        UserDto userDto = userService.getUserFromToken(token);

        UserDto result = userService.editUserInformation(userDto.getId(), userEditForm);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PutMapping("/password/change")
    @ApiOperation(value = "사용자 비밀번호 변경", notes = "사용자의 비밀번호를 변경합니다.")
    public ResponseEntity<?> changeUserPassword(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Valid @RequestBody UserPasswordChangeForm userPasswordChangeForm) {

        UserDto userDto = userService.getUserFromToken(token);

        userService.changeUserPassword(userDto.getId(), userPasswordChangeForm);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/password/reset")
    @ApiOperation(value = "사용자 비밀번호 초기화", notes = "사용자의 비밀번호를 초기화 후 이메일로 새 비밀번호를 전송 합니다.")
    public ResponseEntity<?> resetUserPassword(
            @Valid @RequestBody UserResetPasswordForm userResetPasswordForm) {

        userService.resetUserPassword(userResetPasswordForm.getEmail(), userResetPasswordForm.getNickName());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/signout")
    @ApiOperation(value = "사용자 로그아웃", notes = "사용자의 계정에서 로그아웃합니다.")
    public ResponseEntity<?> signOutUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        UserDto userDto = userService.getUserFromToken(token);

        refreshTokenService.deleteRefreshToken(userDto.getEmail());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/image")
    @ApiOperation(value = "프로필 이미지 업로드", notes = "사용자의 프로필 사진을 이미지서버에 업로드 하고 URL을 사용자정보에 저장합니다.")
    public ResponseEntity<?> uploadImage(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestBody MultipartFile imageFile) throws IOException {

        String url = imageService.saveFile(imageFile);

        UserDto userDto = userService.getUserFromToken(token);

        userService.updateImageUrl(userDto.getId(), url);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/withdrawal")
    @ApiOperation(value = "사용자 회원탈퇴", notes = "사용자 상태를 '삭제' 상태로 변경합니다.")
    public ResponseEntity<?> withdrawUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                          @Valid @RequestBody UserWithdrawalForm userWithdrawalForm) {

        UserDto userDto = userService.getUserFromToken(token);

        userService.withdrawUser(userDto.getId(), userWithdrawalForm);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    //사용자가 이메일 내 링크로 호출하므로 GET 메서드가 호출됨. 따라서 GetMapping 으로 표기.
    @GetMapping("/{id}/verification")
    @ApiOperation(value = "사용자 회원인증", notes = "사용자 상태를 '인증' 상태로 변경합니다.")
    public ResponseEntity<Resource> verifyUser(@PathVariable Long id) {

        Resource resource = new ClassPathResource(userService.verifyUser(id));

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(resource);
    }
}
