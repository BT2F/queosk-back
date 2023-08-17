package com.bttf.queosk.controller;

import com.bttf.queosk.dto.userDto.UserCheckForm;
import com.bttf.queosk.dto.userDto.UserSignInForm;
import com.bttf.queosk.dto.userDto.UserSignUpForm;
import com.bttf.queosk.service.userService.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@Api(tags = "User API", description = "사용자와 관련된 API")
@RequestMapping("/api/users")
@RestController
public class UserController {
    private final UserService userService;

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
}
