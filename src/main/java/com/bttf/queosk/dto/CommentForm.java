package com.bttf.queosk.dto;

import com.bttf.queosk.entity.Comment;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;


public class CommentForm {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel(value = "리뷰 코멘트 Request")
    public static class Request {
        @NotBlank
        private String content;
    }
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel(value = "리뷰 코멘트 Response")
    public static class Response {
        private Long id;
        private String content;

        public static CommentForm.Response of(CommentDto commentDto) {
            return CommentForm.Response.builder()
                    .id(commentDto.getId())
                    .content(commentDto.getContent()).build();
        }
    }

}
