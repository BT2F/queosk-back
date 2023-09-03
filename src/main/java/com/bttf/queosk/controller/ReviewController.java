package com.bttf.queosk.controller;

import com.bttf.queosk.config.JwtTokenProvider;
import com.bttf.queosk.dto.*;
import com.bttf.queosk.entity.Review;
import com.bttf.queosk.service.ReviewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestController
@Api(tags = "Review API", description = "매장 리뷰 API")
@RequestMapping("/api/reviews/")
@RequiredArgsConstructor
public class ReviewController {

    private final JwtTokenProvider jwtTokenProvider;
    private final ReviewService reviewService;

    @PostMapping
    @ApiOperation(value = "리뷰 작성", notes = "사용자가 매장에 대해 리뷰를 남깁니다.")
    public ResponseEntity<Void> createReview(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                               @RequestBody @Valid ReviewCreationForm.Request reviewCreationRequest) {
        Long userId = jwtTokenProvider.getIdFromToken(token);
        reviewService.createReview(userId, reviewCreationRequest);
        return ResponseEntity.status(CREATED).build();
    }

    @GetMapping("{reviewId}")
    @ApiOperation(value = "리뷰 열람", notes = "단건의 리뷰를 열람합니다.")
    public ResponseEntity<GetReviewForm.Response> getReview(@PathVariable("reviewId") Long reviewId) {
        ReviewDto reviewDto = reviewService.getReview(reviewId);
        return ResponseEntity.ok().body(GetReviewForm.Response.of(reviewDto));
    }

    @PutMapping("{reviewId}")
    @ApiOperation(value = "리뷰 수정", notes = "단건의 리뷰를 수정합니다.")
    public ResponseEntity<Void> updateReview(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                               @PathVariable("reviewId") Long reviewId,
                                               @RequestBody @Valid UpdateReviewForm.Request updateReviewRequest) {
        Long userId = jwtTokenProvider.getIdFromToken(token);
        reviewService.updateReview(reviewId, userId, updateReviewRequest);
        return ResponseEntity.status(CREATED).build();
    }

    @DeleteMapping("{reviewId}")
    @ApiOperation(value = "리뷰 삭제", notes = "단건의 리뷰를 삭제합니다.")
    public ResponseEntity<Void> deleteReview(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                               @PathVariable("reviewId") Long reviewId) {
        Long userId = jwtTokenProvider.getIdFromToken(token);
        reviewService.deleteReview(userId, reviewId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("restaurants/{restaurantId}")
    @ApiOperation(value = "매장 리뷰 리스트 열람", notes = "해당 매장에 쓰인 리뷰를 열람합니다.")
    public ResponseEntity<List<GetReviewListForm.Response>> getReviewList(@PathVariable("restaurantId") Long restaurantId) {
        List<ReviewDto> reviewDtoList = reviewService.getReviewList(restaurantId);
        return ResponseEntity.ok(reviewDtoList.stream()
                .map(GetReviewListForm.Response::of)
                .collect(Collectors.toList()));
    }

    @GetMapping("restaurants/{restaurantId}/user")
    @ApiOperation(value = "각 사용자 별 매장 리뷰 열람")
    public ResponseEntity<List<GetRestaurantUserReviewListForm.Response>> getRestaurantUserReviewList(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                                              @PathVariable("restaurantId") Long restaurantId) {
        Long userId = jwtTokenProvider.getIdFromToken(token);
        List<ReviewDto> reviewDtoList = reviewService.getRestaurantUserReviewList(userId, restaurantId);
        return ResponseEntity.ok(reviewDtoList.stream()
                .map(GetRestaurantUserReviewListForm.Response::of)
                .collect(Collectors.toList()));
    }
}
