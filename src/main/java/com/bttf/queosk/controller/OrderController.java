package com.bttf.queosk.controller;


import com.bttf.queosk.config.JwtTokenProvider;
import com.bttf.queosk.dto.*;
import com.bttf.queosk.enumerate.OrderStatus;
import com.bttf.queosk.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@Api(tags = "Order API", description = "주문 관련 API")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("api/user/order")
    @ApiOperation(value = "주문 등록", notes = "매장의 주문을 생성합니다.")
    public ResponseEntity<Void> createOrder(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestBody OrderCreationForm.Request orderCreationForm) {

        Long userId = jwtTokenProvider.getIdFromToken(token);
        orderService.createOrder(orderCreationForm, userId);
        return ResponseEntity.status(CREATED).build();
    }

    @PutMapping("api/restaurant/order/{orderId}")
    @ApiOperation(value = "주문 상태 수정", notes = "해당 주문의 상태를 수정합니다.")
    public ResponseEntity<Void> updateOrderStatus(
            @PathVariable(name = "orderId") Long orderId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestParam OrderStatus orderStatus) {

        Long restaurantId = jwtTokenProvider.getIdFromToken(token);
        orderService.updateOrderStatus(orderId, restaurantId, orderStatus);
        return ResponseEntity.status(CREATED).build();
    }

    @GetMapping("api/restaurant/order/{orderId}")
    @ApiOperation(value = "매장 단일 주문 확인", notes = "매장에서 단일한 주문의 내용을 확인합니다")
    public ResponseEntity<ReadOrderForm.Response> readOrder(
            @PathVariable(name = "orderId") Long orderId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        Long restaurantId = jwtTokenProvider.getIdFromToken(token);
        ReadOrderForm.Response response = ReadOrderForm.Response
                .of(orderService.readOrder(orderId, restaurantId));
        return ResponseEntity.status(OK).body(response);
    }

    @GetMapping("api/restaurant/orders/today")
    @ApiOperation(value = "매장 금일 주문 리스트 확인", notes = "매장에서 오늘 주문한 리스트를 확인합니다")
    public ResponseEntity<List<ReadTodayOrderListForm.Response>> readTodayOrderList(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        Long restaurantId = jwtTokenProvider.getIdFromToken(token);
        List<OrderDto> orderDtoList = orderService.readTodayOrderList(restaurantId);
        List<ReadTodayOrderListForm.Response> responses = orderDtoList
                .stream()
                .map(ReadTodayOrderListForm.Response::of)
                .collect(Collectors.toList());
        return ResponseEntity.status(OK).body(responses);
    }

    @GetMapping("api/restaurant/orders/in-progress")
    @ApiOperation(value = "매장 주문처리중 리스트 확인", notes = "매장에서 현재 주문처리중인 주문의 리스트를 확인합니다")
    public ResponseEntity<List<ReadInProgressOrderListForm.Response>> readInProgressOrderList(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        Long restaurantId = jwtTokenProvider.getIdFromToken(token);
        List<OrderDto> inProgressOrderDtoList = orderService.readInProgressOrderList(restaurantId);
        List<ReadInProgressOrderListForm.Response> responses = inProgressOrderDtoList
                .stream().map(ReadInProgressOrderListForm.Response::of)
                .collect(Collectors.toList());
        return ResponseEntity.status(OK).body(responses);
    }

    @GetMapping("api/restaurant/orders/today-done")
    @ApiOperation(value = "매장 금일 주문 처리 완료 리스트 확인", notes = "매장에서 오늘 주문 처리 완료한 리스트를 확인합니다")
    public ResponseEntity<List<ReadInProgressOrderListForm.Response>> readTodayDoneList(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        Long restaurantId = jwtTokenProvider.getIdFromToken(token);
        List<OrderDto> doneList = orderService.readItodayDoneList(restaurantId);
        List<ReadInProgressOrderListForm.Response> responses = doneList
                .stream().map(ReadInProgressOrderListForm.Response::of)
                .collect(Collectors.toList());
        return ResponseEntity.status(OK).body(responses);
    }
}
