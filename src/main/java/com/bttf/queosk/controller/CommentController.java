package com.bttf.queosk.controller;

import com.bttf.queosk.config.JwtTokenProvider;
import com.bttf.queosk.dto.CommentForm;
import com.bttf.queosk.service.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = "Review Comment API", description = "매장 리뷰 코멘트 API")
@RequestMapping("/api/reviews/")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("{reviewId}/comments")
    @ApiOperation(value = "리뷰 코멘트 작성", notes = "리뷰의 코멘트를 작성합니다.")
    public ResponseEntity<Object> createComment(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                                @PathVariable(value = "reviewId") Long reviewId,
                                                @RequestBody CommentForm commentForm) {
        Long restaurantId = jwtTokenProvider.getIdFromToken(token);

        commentService.createComment(reviewId, restaurantId, commentForm);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("comments/{commentId}")
    @ApiOperation(value = "리뷰 코멘트 수정", notes = "리뷰의 코멘트를 수정합니다.")
    ResponseEntity<Object> updateComment(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                         @PathVariable(value = "commentId") Long commentId,
                                         @RequestBody CommentForm commentForm) {

        Long restaurantId = jwtTokenProvider.getIdFromToken(token);

        commentService.updateComment(commentId, restaurantId, commentForm);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("comments/{commentId}")
    @ApiOperation(value = "리뷰 코멘트 삭제", notes = "리뷰의 코멘트를 삭제합니다.")
    ResponseEntity<Object> deleteComment(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                         @PathVariable(value = "commentId") Long commentId) {
        Long restaurantId = jwtTokenProvider.getIdFromToken(token);

        commentService.deleteComment(commentId, restaurantId);

        return ResponseEntity.status(204).build();
    }

    @GetMapping("{reviewId}/comments")
    @ApiOperation(value = "리뷰 코멘트 열람", notes = "해당 리뷰의 코멘트 리스트를 열람합니다.")
    ResponseEntity<Object> getComment(@PathVariable(value = "reviewId") Long reviewId){
        return ResponseEntity.ok().body(commentService.getComment(reviewId));
    }
}
