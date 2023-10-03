package com.bttf.queosk.controller;

import com.bttf.queosk.config.JwtTokenProvider;
import com.bttf.queosk.dto.*;
import com.bttf.queosk.service.KakaoPaymentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.*;

@RestController
@Api(tags = "Kakao Pay API", description = "Kakao Pay 결제 API")
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class KakaoPayController {
    private final KakaoPaymentService kakaoPaymentService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/ready")
    @ApiOperation(value = "결제 준비 API", notes = "결제를 준비하는 API를 작성 합니다.")
    public ResponseEntity<KakaoPaymentReadyResponseForm> paymentReady(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Valid @RequestBody KakaoPaymentReadyRequestForm kakaoPaymentReadyRequestForm) {

        Long userId = jwtTokenProvider.getIdFromToken(token);

        KakaoPaymentReadyResponseForm paymentReady =
                kakaoPaymentService.kakaoPaymentReady(userId, kakaoPaymentReadyRequestForm);

        return ResponseEntity.status(OK).body(paymentReady);
    }

    @PostMapping("/approve")
    @ApiOperation(value = "결제 승인 API", notes = "결제를 승인받는 API를 작성 합니다.")
    public ResponseEntity<KakaoPaymentApprovalResponseForm> paymentApprove(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestParam("pg_token") String pgToken,
            @Valid @RequestBody KakaoPaymentApprovalRequestForm kakaoPaymentApprovalRequestForm) {

        Long userId = jwtTokenProvider.getIdFromToken(token);

        KakaoPaymentApprovalResponseForm paymentApprove =
                kakaoPaymentService.kakaoPaymentApprove(userId, pgToken, kakaoPaymentApprovalRequestForm);

        return ResponseEntity.status(OK).body(paymentApprove);
    }

    @PostMapping("/cancel")
    @ApiOperation(value = "결제 취소 API", notes = "결제를 취소하는 API를 작성 합니다.")
    public ResponseEntity<KakaoPaymentCancelResponseForm> paymentCancel(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Valid @RequestBody KakaoPaymentCancelRequestForm kakaoPaymentCancelRequestForm) {

        Long userId = jwtTokenProvider.getIdFromToken(token);

        KakaoPaymentCancelResponseForm paymentCancel =
                kakaoPaymentService.kakaoPaymentCancel(userId, kakaoPaymentCancelRequestForm);

        return ResponseEntity.status(OK).body(paymentCancel);
    }
}
