package com.bttf.queosk.controller;


import com.bttf.queosk.config.JwtTokenProvider;
import com.bttf.queosk.dto.OrderCreationForm;
import com.bttf.queosk.dto.OrderDto;
import com.bttf.queosk.enumerate.OrderStatus;
import com.bttf.queosk.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@Api(tags = "Order API", description = "주문 관련 API")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("api/user/order")
    @ApiOperation(value = "주문 등록", notes = "매장의 주문을 생성합니다.")
    public ResponseEntity<?> createOrder(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                         @RequestBody OrderCreationForm orderCreationForm) {
        Long userId = jwtTokenProvider.getIdFromToken(token);
        orderService.createOrder(orderCreationForm, userId);
        return ResponseEntity.status(CREATED).build();
    }

    @PutMapping("api/restaurant/order/{orderId}")
    @ApiOperation(value = "주문 상태 수정", notes = "해당 주문의 상태를 수정합니다.")
    public ResponseEntity<?> updateOrderStatus(@PathVariable(name = "orderId") Long orderId,
                                               @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                               @RequestBody OrderStatus orderStatus) {
        Long restaurantId = jwtTokenProvider.getIdFromToken(token);
        orderService.updateOrderStatus(orderId, restaurantId, orderStatus);
        return ResponseEntity.status(204).build();
    }

    @GetMapping("api/restaurant/order/{orderId}")
    @ApiOperation(value = "매장 단일 주문 확인", notes = "매장에서 단일한 주문의 내용을 확인합니다")
    public ResponseEntity<?> readOrder(@PathVariable(name = "orderId") Long orderId,
                                       @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        Long restaurantId = jwtTokenProvider.getIdFromToken(token);
        return ResponseEntity.status(200).body(orderService.readOrder(orderId, restaurantId));
    }

    @GetMapping("api/restaurant/orders/today")
    @ApiOperation(value = "매장 금일 주문 리스트 확인", notes = "매장에서 오늘 주문한 리스트를 확인합니다")
    public ResponseEntity<?> readTodayOrderList(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        Long restaurantId = jwtTokenProvider.getIdFromToken(token);
        List<OrderDto> orderDtoList = orderService.readTodayOrderList(restaurantId);
        return ResponseEntity.ok().body(orderDtoList);
    }

    @GetMapping("api/restaurant/orders/in-progress")
    @ApiOperation(value = "매장 주문처리중 리스트 확인", notes = "매장에서 현재 주문처리중인 주문의 리스트를 확인합니다")
    public ResponseEntity<?> readInProgressOrderList(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        Long restaurantId = jwtTokenProvider.getIdFromToken(token);
        List<OrderDto> inProgressOrderDtoList = orderService.readInProgressOrderList(restaurantId);
        return ResponseEntity.ok().body(inProgressOrderDtoList);
    }
}
