package com.project.uandmeet.dto.commentsDtoGroup;

import com.project.uandmeet.model.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CommentsReponseDto {
    private Long id;
    private LocalDateTime createdAt;
    private String centent;

    public CommentsReponseDto(Comment comment) {
        this.id = comment.getId();
        this.centent = comment.getComment();
        this.createdAt = comment.getCreatedAt();
    }
}
