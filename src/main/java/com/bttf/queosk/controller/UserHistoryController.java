package com.bttf.queosk.controller;

import com.bttf.queosk.config.JwtTokenProvider;
import com.bttf.queosk.dto.UserHistoryDto;
import com.bttf.queosk.dto.UserHistoryResponseForm;
import com.bttf.queosk.service.UserHistoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.OK;

@RequiredArgsConstructor
@RequestMapping("/api/users/history")
@Api(tags = "User History API", description = "고객 히스토리 API")
@RestController
public class UserHistoryController {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserHistoryService userHistoryService;

    @GetMapping
    @ApiOperation(value = "히스토리 목록조회", notes = "전체 히스토리 목록을 조회합니다.")
    public ResponseEntity<?> getUserHistories(
            @RequestHeader(AUTHORIZATION) String token) {

        Long userId = jwtTokenProvider.getIdFromToken(token);

        List<UserHistoryDto> userHistories = userHistoryService.getUserHistories(userId);

        return ResponseEntity.status(OK).body(UserHistoryResponseForm.of(userHistories));
    }
}