package com.bttf.queosk.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "리뷰 코멘트 Response")
public class CommentResponseForm {

    private Long id;
    private String content;

    public static CommentResponseForm of(CommentDto commentDto) {
        return CommentResponseForm.builder()
                .id(commentDto.getId())
                .content(commentDto.getContent()).build();
    }
}