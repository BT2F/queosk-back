package com.bttf.queosk.controller;

import com.bttf.queosk.config.JwtTokenProvider;
import com.bttf.queosk.dto.SettlementPriceForm;
import com.bttf.queosk.dto.SettlementResponseForm;
import com.bttf.queosk.service.SettlementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.springframework.http.HttpStatus.OK;

@RequestMapping("/api/restaurants/settlement")
@Api(tags = "Settlement API", description = "정산 관련 API")
@RestController
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementService settlementService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/today")
    @ApiOperation(value = "매장 금일 정산", notes = "매장의 오늘 정산 현황을 알 수 있습니다.")
    public ResponseEntity<SettlementResponseForm> getTodaySettlement(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        Long restaurantId = jwtTokenProvider.getIdFromToken(token);

        LocalDate today = LocalDate.now();

        return ResponseEntity.status(OK)
                .body(SettlementResponseForm.of(settlementService.SettlementGet(
                        restaurantId, getLocalDateTimeOfFrom(today), getLocalDateTimeOfTo(today)
                )));
    }

    @GetMapping("/period")
    @ApiOperation(value = "매장 기간별 정산", notes = "매장의 기간별 정산 현황을 알 수 있습니다.")
    public ResponseEntity<SettlementResponseForm> getPeriodSettlement(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from) {

        Long restaurantId = jwtTokenProvider.getIdFromToken(token);

        return ResponseEntity.status(OK)
                .body(SettlementResponseForm.of(settlementService.SettlementGet(
                        restaurantId, getLocalDateTimeOfFrom(from), getLocalDateTimeOfTo(to)
                )));
    }

    @GetMapping("/period/price")
    @ApiOperation(value = "매장 기간별 정산 금액", notes = "매장의 기간별 정산 금액을 알 수 있습니다.")
    public ResponseEntity<SettlementPriceForm.Response> getPeriodSettlementPrice(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from) {

        Long restaurantId = jwtTokenProvider.getIdFromToken(token);

        return ResponseEntity.status(OK)
                .body(SettlementPriceForm.Response.of(settlementService.periodSettlementPriceGet(
                        restaurantId, getLocalDateTimeOfFrom(from), getLocalDateTimeOfTo(to)
                )));
    }

    private LocalDateTime getLocalDateTimeOfTo(LocalDate localDate) {
        return LocalDateTime.of(localDate, LocalTime.MAX);
    }

    private LocalDateTime getLocalDateTimeOfFrom(LocalDate localDate) {
        return LocalDateTime.of(localDate, LocalTime.MIN);
    }
}
