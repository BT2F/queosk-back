package com.bttf.queosk.controller;

import com.bttf.queosk.dto.TokenRefreshDto;
import com.bttf.queosk.dto.TokenRefreshForm;
import com.bttf.queosk.service.RefreshTokenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.OK;

@RequiredArgsConstructor
@Api(tags = "Refresh Token API", description = "Refresh Token을 이용해 Access Token을 발급받는 Api")
@RestController
public class RefreshTokenController {
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/api/auth/refresh")
    @ApiOperation(value = "Access 토큰 재발급(고객)", notes = "Refresh 토큰을 입력하여 Access Token을 재발급 받습니다.")
    public ResponseEntity<TokenRefreshForm.Response> reissueAccessTokenForUser(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Valid @RequestBody TokenRefreshForm.Request tokenRefreshRequest) {

        String refreshToken = tokenRefreshRequest.getRefresh_token();

        // 토큰 재발급 서비스 호출
        TokenRefreshDto tokenRefreshDto =
                refreshTokenService.issueNewAccessToken(token, refreshToken);

        return ResponseEntity.status(OK).body(TokenRefreshForm.Response.of(tokenRefreshDto));
    }
}
