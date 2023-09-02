package com.bttf.queosk.controller;

import com.bttf.queosk.config.JwtTokenProvider;
import com.bttf.queosk.dto.*;
import com.bttf.queosk.service.QueueService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@Api(tags = "Queue API", description = "매장 웨이팅 관련 API")
@RequestMapping("/api/restaurants")
@RestController
public class QueueController {
    private final QueueService queueService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/{restaurantId}/queue")
    @ApiOperation(value = "가게 웨이팅 등록", notes = "유저가 가게의 웨이팅을 등록할 수 있습니다.")
    public ResponseEntity<Void> queueCreate(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Valid @RequestBody QueueCreateForm.Request queueRequestRequest,
            @PathVariable Long restaurantId) {

        Long userId = jwtTokenProvider.getIdFromToken(token);

        queueService.createQueue(queueRequestRequest, userId, restaurantId);

        return ResponseEntity.status(CREATED).build();
    }

    @GetMapping("/queue")
    @ApiOperation(value = "가게의 현재 웨이팅 팀들의 정보를 알 수 있습니다.",
            notes = "가게의 현재 웨이팅 팀들의 정보를 알 수 있습니다.")
    public ResponseEntity<QueueListForm.Response> queueTeamListInfoList(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        Long restaurantId = jwtTokenProvider.getIdFromToken(token);

        QueueListDto queueListDto = queueService.getQueueList(restaurantId);

        return ResponseEntity.status(OK).body(QueueListForm.Response.of(queueListDto));
    }

    @GetMapping("/{restaurantId}/user/queue")
    @ApiOperation(value = "유저의 웨이팅 순서를 알 수 있습니다.", notes = "현재 유저의 웨이팅 순서를 알 수 있습니다.")
    public ResponseEntity<QueueIndexForm.Response> queueUserWaitingNumberList(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable Long restaurantId) {

        Long userId = jwtTokenProvider.getIdFromToken(token);

        QueueIndexDto queueIndexDto =
                queueService.getUserQueueNumber(restaurantId, userId);

        return ResponseEntity.status(OK).body(QueueIndexForm.Response.of(queueIndexDto));
    }

    @DeleteMapping("/{restaurantId}/user/queue")
    @ApiOperation(value = "사용자 본인의 웨이팅을 취소합니다.", notes = "사용자가 본인의 웨이팅을 취소합니다.")
    public ResponseEntity<Void> queueUserWaitingNumberRemove(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable Long restaurantId) {

        Long userId = jwtTokenProvider.getIdFromToken(token);

        queueService.deleteUserQueue(restaurantId, userId);

        return ResponseEntity.status(NO_CONTENT).build();
    }

    @DeleteMapping("/queue")
    @ApiOperation(value = "첫 번째 대기팀이 식당 진입이 가능할 경우 호출",
            notes = "첫 번째 대기팀을 대기열에서 Pop 하고 갱신된 대기열리스트를 반환합니다.")
    public ResponseEntity<Void> queueWaitingTeamDelete(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        Long restaurantId = jwtTokenProvider.getIdFromToken(token);

        queueService.popTheFirstTeamOfQueue(restaurantId);

        return ResponseEntity.status(NO_CONTENT).build();
    }
}
