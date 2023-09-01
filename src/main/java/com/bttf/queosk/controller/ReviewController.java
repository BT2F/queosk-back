package com.bttf.queosk.controller;

import com.bttf.queosk.config.JwtTokenProvider;
import com.bttf.queosk.dto.ReviewCreationForm;
import com.bttf.queosk.dto.UpdateReviewForm;
import com.bttf.queosk.service.ReviewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Api(tags = "Review API", description = "매장 리뷰 API")
@RequestMapping("/api/reviews/")
@RequiredArgsConstructor
public class ReviewController {

    private final JwtTokenProvider jwtTokenProvider;
    private final ReviewService reviewService;

    @PostMapping
    @ApiOperation(value = "리뷰 작성", notes = "사용자가 매장에 대해 리뷰를 남깁니다.")
    public ResponseEntity<Object> createReview(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                               @RequestBody @Valid ReviewCreationForm reviewCreationForm) {
        Long userId = jwtTokenProvider.getIdFromToken(token);
        reviewService.createReview(userId, reviewCreationForm);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("{reviewId}")
    @ApiOperation(value = "리뷰 열람", notes = "단건의 리뷰를 열람합니다.")
    public ResponseEntity<Object> getReview(@PathVariable("reviewId") Long reviewId) {
        return ResponseEntity.ok().body(reviewService.getReview(reviewId));
    }

    @PutMapping("{reviewId}")
    @ApiOperation(value = "리뷰 수정", notes = "단건의 리뷰를 수정합니다.")
    public ResponseEntity<Object> updateReview(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                               @PathVariable("reviewId") Long reviewId,
                                               @RequestBody @Valid UpdateReviewForm updateReviewForm) {
        Long userId = jwtTokenProvider.getIdFromToken(token);
        reviewService.updateReview(reviewId, userId, updateReviewForm);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{reviewId}")
    @ApiOperation(value = "리뷰 삭제", notes = "단건의 리뷰를 삭제합니다.")
    public ResponseEntity<Object> deleteReview(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                               @PathVariable("reviewId") Long reviewId) {
        Long userId = jwtTokenProvider.getIdFromToken(token);
        reviewService.deleteReview(userId, reviewId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("restaurant/{restaurantId}")
    @ApiOperation(value = "매장 리뷰 리스트 열람", notes = "해당 매장에 쓰인 리뷰를 열람합니다.")
    public ResponseEntity<Object> getReviewList(@PathVariable("restaurantId") Long restaurantId) {
        return ResponseEntity.ok().body(reviewService.getReviewList(restaurantId));
    }

    @GetMapping("restaurant/{restaurantId}/user")
    @ApiOperation(value = "각 사용자 별 매장 리뷰 열람")
    public ResponseEntity<Object> getRestaurantUserReviewList(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                                              @PathVariable("restaurantId") Long restaurantId) {
        Long userId = jwtTokenProvider.getIdFromToken(token);
        return ResponseEntity.ok().body(reviewService.getRestaurantUserReviewList(userId, restaurantId));
    }

}
