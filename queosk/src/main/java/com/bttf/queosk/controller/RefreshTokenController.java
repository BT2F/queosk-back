package com.bttf.queosk.controller;

import com.bttf.queosk.dto.tokendto.NewAccessTokenDto;
import com.bttf.queosk.dto.tokendto.RefreshTokenForm;
import com.bttf.queosk.service.refreshtokenservice.RefreshTokenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@Api(tags = "Refresh Token API", description = "Refresh Token을 이용해 Access Token을 발급받는 Api")
@RequestMapping("/api")
@RestController
public class RefreshTokenController {
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/users/auth/reissue")
    @ApiOperation(value = "Access 토큰 재발급(고객)", notes = "Refresh 토큰을 입력하여 Access Token을 재발급 받습니다.")
    public ResponseEntity<?> reissueAccessTokenForUser(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Valid @RequestBody RefreshTokenForm refreshTokenForm) {

        NewAccessTokenDto newAccessTokenDto =
                refreshTokenService.issueNewAccessTokenForUser(refreshTokenForm.getRefresh_token());

        return ResponseEntity.status(HttpStatus.OK).body(newAccessTokenDto);
    }

    @PostMapping("/restaurant/auth/reissue")
    @ApiOperation(value = "Access 토큰 재발급(점주)", notes = "Refresh 토큰을 입력하여 Access Token을 재발급 받습니다.")
    public ResponseEntity<?> reissueAccessTokenForRestaurant(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Valid @RequestBody RefreshTokenForm refreshTokenForm) {

        NewAccessTokenDto newAccessTokenDto =
                refreshTokenService.issueNewAccessTokenForRestaurant(refreshTokenForm.getRefresh_token());

        return ResponseEntity.status(HttpStatus.OK).body(newAccessTokenDto);
    }
}
