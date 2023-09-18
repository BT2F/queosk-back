package com.bttf.queosk.controller;

import com.bttf.queosk.dto.KakaoLoginForm;
import com.bttf.queosk.dto.UserSignInDto;
import com.bttf.queosk.service.KakaoLoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static org.springframework.http.HttpStatus.OK;

@RestController
@Api(tags = "Kakao Login API", description = "Kakao 소셜 로그인 API")
@RequiredArgsConstructor
@RequestMapping("/api/kakao")
public class KakaoLoginController {
    private final KakaoLoginService kakaoLoginService;

    @PostMapping("/signin")
    @ApiOperation(value = "카카오톡 소셜 로그인 코드를 사용하여 로그인진행",
            notes = "카카오톡 로그인 URL을 통해 얻은 code 및 데이터를 사용하여 로그인 또는 회원가입을 진행합니다.")
    public ResponseEntity<KakaoLoginForm.Response> kakaoLoginCallback(
            HttpServletRequest request,
            @Valid @RequestBody KakaoLoginForm.Request kaKaoLoginRequest) {

        String host = request.getHeader("Host");

        UserSignInDto userSignInDto =
            host.contains("localhost")?
            kakaoLoginService.getUserInfoFromKakaoTest(kaKaoLoginRequest):
            kakaoLoginService.getUserInfoFromKakao(kaKaoLoginRequest);

        return ResponseEntity.status(OK).body(KakaoLoginForm.Response.of(userSignInDto));
    }
}
