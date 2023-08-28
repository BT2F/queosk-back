package com.bttf.queosk.controller;

import com.bttf.queosk.config.springsecurity.JwtTokenProvider;
import com.bttf.queosk.dto.KakaoPaymentReadyDto;
import com.bttf.queosk.dto.KakaoPaymentReadyForm;
import com.bttf.queosk.service.kakaoservice.KakaoPaymentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Api(tags = "Kakao Pay API", description = "Kakao Pay 결제 API")
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class KakaoPayController {
    private final KakaoPaymentService kakaoPaymentService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/ready")
    @ApiOperation(value = "결제 준비 API", notes = "결제를 준비하는 API를 작성 합니다.")
    public ResponseEntity<Object> paymentReady(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @Valid @RequestBody KakaoPaymentReadyForm kakaoPaymentReadyForm) {
        Long userId = jwtTokenProvider.getIdFromToken(token);
        KakaoPaymentReadyDto paymentReady = kakaoPaymentService.kakaoPaymentReady(userId, kakaoPaymentReadyForm);
        return ResponseEntity.ok().body(paymentReady);
    }
}
