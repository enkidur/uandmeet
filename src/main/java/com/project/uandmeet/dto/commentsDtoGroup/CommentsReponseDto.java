package com.project.uandmeet.dto.commentsDtoGroup;

import com.project.uandmeet.model.BaseEntity;
import com.project.uandmeet.model.BaseTime;
import com.project.uandmeet.model.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentsReponseDto {
    private Long id;
    private String content;
    private String nicname;
    private String profile;


    public CommentsReponseDto(Comment comment, String nicname, String profile) {
        this.content = comment.getComment();
        this.nicname = nicname;
        this.profile = profile;
    }
}
