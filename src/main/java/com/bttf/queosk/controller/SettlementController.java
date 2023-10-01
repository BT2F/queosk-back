package com.bttf.queosk.controller;

import com.bttf.queosk.config.JwtTokenProvider;
import com.bttf.queosk.dto.SettlementForm;
import com.bttf.queosk.dto.SettlementPriceForm;
import com.bttf.queosk.service.SettlementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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
    public ResponseEntity<SettlementForm.Response> getTodaySettlement(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        Long restaurantId = jwtTokenProvider.getIdFromToken(token);
        return ResponseEntity.status(OK)
                .body(SettlementForm.Response.of(settlementService.todaySettlementGet(restaurantId)));
    }

    @GetMapping("/period")
    @ApiOperation(value = "매장 기간별 정산", notes = "매장의 기간별 정산 현황을 알 수 있습니다.")
    public ResponseEntity<SettlementForm.Response> getPeriodSettlement(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from) {


        Long restaurantId = jwtTokenProvider.getIdFromToken(token);
        return ResponseEntity.status(OK)
                .body(SettlementForm.Response.of(settlementService.periodSettlementGet(
                        restaurantId, to.atStartOfDay(), from.atStartOfDay()
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
                        restaurantId, to.atStartOfDay(), from.atStartOfDay()
                )));
    }
}
