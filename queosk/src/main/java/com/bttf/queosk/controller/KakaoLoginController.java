package com.bttf.queosk.controller;

import com.bttf.queosk.dto.kakaodto.KaKaoLoginForm;
import com.bttf.queosk.dto.userdto.UserSignInDto;
import com.bttf.queosk.service.kakaoservice.KakaoLoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Api(tags = "Kakao Login API", description = "Kakao 소셜 로그인 API")
@RequiredArgsConstructor
@RequestMapping("/api/kakao")
public class KakaoLoginController {
    private final KakaoLoginService kakaoLoginService;

    @GetMapping("/signin")
    @ApiOperation(value = "카카오톡 소셜 로그인 경로 호출", notes = "카카오톡 로그인 URL을 가져옵니다")
    public ResponseEntity<?> kakaoLogin() {
        return ResponseEntity.status(HttpStatus.OK).body(kakaoLoginService.getKakaoLogin());
    }

    @PostMapping("/signin")
    @ApiOperation(value = "카카오톡 소셜 로그인 코드를 사용하여 로그인진행",
            notes = "카카오톡 로그인 URL을 통해 얻은 code 및 데이터를 사용하여 로그인 또는 회원가입을 진행합니다.")
    public ResponseEntity<?> kakaoLoginCallback(
            @Valid @RequestBody KaKaoLoginForm kaKaoLoginForm) {

        UserSignInDto userSignInDto =
                kakaoLoginService.getKakaoInfo(kaKaoLoginForm);

        return ResponseEntity.ok().body(userSignInDto);
    }
}
