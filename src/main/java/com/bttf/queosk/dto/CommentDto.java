package com.bttf.queosk.dto;

import com.bttf.queosk.entity.Comment;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "Comment DTO")
public class CommentDto {
    private Long id;
    private String content;

    public static CommentDto of(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent()).build();
    }
}
