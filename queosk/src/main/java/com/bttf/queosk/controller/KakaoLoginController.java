package com.bttf.queosk.controller;

import com.bttf.queosk.dto.userdto.UserSignInDto;
import com.bttf.queosk.service.kakaoservice.KakaoLoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@Api(tags = "Kakao Login API", description = "Kakao 소셜 로그인 API")
@RequiredArgsConstructor
@RequestMapping("kakao")
public class KakaoLoginController {
    private final KakaoLoginService kakaoLoginService;

    @GetMapping("/signin")
    @ApiOperation(value = "카카오톡 소셜 로그인", notes = "카카오톡계정을 사용해 소셜로그인을 진행합니다.")
    public ResponseEntity<?> kakaoLogin() {
        return ResponseEntity.status(HttpStatus.OK).body(kakaoLoginService.getKakaoLogin());
    }

    @GetMapping("/signin/callback")
    @ApiOperation(value = "카카오톡 소셜 로그인 콜백", notes = "카카오톡 소셜 로그인 리다이렉트 경로입니다.")
    public ResponseEntity<?> kakaoLoginCallback(HttpServletRequest request) {

        UserSignInDto userSignInDto =
                kakaoLoginService.getKakaoInfo(request.getParameter("code"));

        return ResponseEntity.ok().body(userSignInDto);
    }
}
