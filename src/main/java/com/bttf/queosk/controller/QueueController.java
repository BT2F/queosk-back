package com.bttf.queosk.controller;

import com.bttf.queosk.config.JwtTokenProvider;
import com.bttf.queosk.dto.queuedto.QueueCreateForm;
import com.bttf.queosk.dto.queuedto.QueueDto;
import com.bttf.queosk.dto.queuedto.QueueForm;
import com.bttf.queosk.service.QueueService;
import com.bttf.queosk.service.QueueWaitingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@Api(tags = "Queue API", description = "매장 웨이팅 등록")
@RequestMapping("/api/restaurants/queue")
@RestController
public class QueueController {
    private final QueueService queueService;
    private final JwtTokenProvider jwtTokenProvider;
    private final QueueWaitingService queueWaitingService;

    @PostMapping("/{restaurantId}")
    @ApiOperation(value = "가게 웨이팅 등록", notes = "유저가 가게의 웨이팅을 등록할 수 있습니다.")
    public ResponseEntity<?> queueCreate(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                         @Valid @RequestBody QueueCreateForm queueRequestForm,
                                         @PathVariable Long restaurantId) {

        Long userId = jwtTokenProvider.getIdFromToken(token);

        Long userIndex = queueService.createQueue(queueRequestForm, userId, restaurantId);

        //웹소켓으로 해당 RestaurantId 를구독하고 있는 페이지에 대기인원수 업데이트
        queueWaitingService.updateWaitingCount(restaurantId);

        queueWaitingService.updateUserIndexes(restaurantId);

        return ResponseEntity.status(HttpStatus.CREATED).body(userIndex);
    }

    @GetMapping("/list/info")
    @ApiOperation(value = "가게의 현재 웨이팅 팀들의 정보를 알 수 있습니다.",
            notes = "가게의 현재 웨이팅 팀들의 정보를 알 수 있습니다.")
    public ResponseEntity<List<QueueForm.Response>> queueTeamListInfoGet(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        Long restaurantId = jwtTokenProvider.getIdFromToken(token);
        return ResponseEntity.status(HttpStatus.OK)
                .body(queueService.getQueueList(restaurantId));
    }

    @GetMapping("/{restaurantId}/user")
    @ApiOperation(value = "유저의 웨이팅 순서를 알 수 있습니다.",
            notes = "현재 유저의 웨이팅 순서를 알 수 있습니다.")
    public ResponseEntity<Long> queueUserWaitingNumberGet(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable Long restaurantId
    ) {
        Long userId = jwtTokenProvider.getIdFromToken(token);
        return ResponseEntity.status(HttpStatus.OK)
                .body(queueService.getUserQueueNumber(restaurantId, userId));
    }

    @DeleteMapping
    @ApiOperation(value = "가게의 웨이팅 팀의 대기 순서를 삭제 할 수 있습니다.",
            notes = "점주가 손님이 나간 경우에 웨이팅 대기열에 있는 팀의 순서를 삭제 할 수 있습니다.")
    public ResponseEntity<?> queueWaitingTeamDelete(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        Long restaurantId = jwtTokenProvider.getIdFromToken(token);

        List<QueueDto> queues =
                queueService.popTheFirstTeamOfQueue(restaurantId);

        queueWaitingService.updateWaitingCount(restaurantId);

        queueWaitingService.updateUserIndexes(restaurantId);

        return ResponseEntity.status(HttpStatus.OK).body(queues);
    }
}
