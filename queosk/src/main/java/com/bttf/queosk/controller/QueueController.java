package com.bttf.queosk.controller;

import com.bttf.queosk.dto.queuedto.QueueForm;
import com.bttf.queosk.dto.userdto.UserDto;
import com.bttf.queosk.service.queueservice.QueueService;
import com.bttf.queosk.service.userservice.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Api(tags = "Queue API", description = "매장 웨이팅 등록")
@RequestMapping("/api/restaurants/{restaurantId}/queue")
@RestController
public class QueueController {
    private final QueueService queueService;
    private final UserService userService;

    @PostMapping
    @ApiOperation(value = "가게 웨이팅 등록", notes = "유저가 가게의 웨이팅을 등록할 수 있습니다.")
    public ResponseEntity<?> queueCreate(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                         @RequestBody final QueueForm.Request queueRequestForm,
                                         @PathVariable Long restaurantId) {

        UserDto userFromToken = userService.getUserFromToken(token);

        Long queue = queueService.createQueue(queueRequestForm, userFromToken.getId(), restaurantId);

        return ResponseEntity.status(HttpStatus.CREATED).body(queue);
    }


    @GetMapping("/list")
    @ApiOperation(value = "가게의 현재 웨이팅 팀 수", notes = "가게의 현재 웨이팅 팀 수를 알 수 있습니다.")
    public ResponseEntity<Integer> queueCountingTeamsGet(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable Long restaurantId) {
        UserDto userFromToken = userService.getUserFromToken(token);
        return ResponseEntity.status(HttpStatus.OK).body(queueService.getQueueCountingTeams(restaurantId, userFromToken.getId()));
    }

    @GetMapping("/list/info")
    @ApiOperation(value = "가게의 현재 웨이팅 팀들의 정보를 알 수 있습니다.",
            notes = "가게의 현재 웨이팅 팀들의 정보를 알 수 있습니다.")
    public ResponseEntity<List<QueueForm.Response>> queueTeamListInfoGet(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable Long restaurantId) {
        UserDto userFromToken = userService.getUserFromToken(token);
        return ResponseEntity.status(HttpStatus.OK)
                .body(queueService.getQueueTeamListInfo(restaurantId, userFromToken.getId()));
    }

    @GetMapping("/user")
    @ApiOperation(value = "유저의 웨이팅 순서를 알 수 있습니다.",
            notes = "현재 유저의 웨이팅 순서를 알 수 있습니다.")
    public ResponseEntity<Long> queueUserWaitingNumberGet(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable Long restaurantId
    ) {
        UserDto userFromToken = userService.getUserFromToken(token);
        return ResponseEntity.status(HttpStatus.OK)
                .body(queueService.getQueueUserWaitingCount(restaurantId, userFromToken.getId()));
    }

    @DeleteMapping()
    @ApiOperation(value = "가게의 웨이팅 팀의 대기 순서를 삭제 할 수 있습니다.",
            notes = "점주가 손님이 나간 경우에 웨이팅 대기열에 있는 팀의 순서를 삭제 할 수 있습니다.")
    public ResponseEntity<?> queueWaitingTeamDelete(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable Long restaurantId
    ) {
        UserDto userFromToken = userService.getUserFromToken(token);
        queueService.deleteQueueWaitingTeam(userFromToken.getId(), restaurantId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
