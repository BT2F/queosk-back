package com.bttf.queosk.dto;

import com.bttf.queosk.entity.Comment;
import com.bttf.queosk.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDto {
    private String content;

    public static CommentDto of(Comment comment) {
        return CommentDto.builder().content(comment.getContent()).build();
    }
}
