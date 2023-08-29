package com.bttf.queosk.controller;

import com.bttf.queosk.config.JwtTokenProvider;
import com.bttf.queosk.dto.CreateReviewForm;
import com.bttf.queosk.service.ReviewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = "Restaurant API", description = "매장 리뷰 API")
@RequestMapping("/api/reviews/")
@RequiredArgsConstructor
public class ReviewController {

    private final JwtTokenProvider jwtTokenProvider;
    private final ReviewService reviewService;

    @PostMapping
    @ApiOperation(value = "리뷰 작성", notes = "사용자가 매장에 대해 리뷰를 남깁니다.")
    public ResponseEntity<Object> createReview(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                               @RequestBody CreateReviewForm createReviewForm) {
        Long userId = jwtTokenProvider.getIdFromToken(token);
        reviewService.createReview(userId, createReviewForm);
        return ResponseEntity.ok().build();
    }

}
