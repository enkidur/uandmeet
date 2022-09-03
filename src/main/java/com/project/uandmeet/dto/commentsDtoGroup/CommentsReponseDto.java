package com.project.uandmeet.dto.commentsDtoGroup;

import com.project.uandmeet.model.BaseEntity;
import com.project.uandmeet.model.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentsReponseDto extends BaseEntity {
    private Long id;
    private String centent;
    private String nicname;
    private String profile;

    public CommentsReponseDto(Comment comment, String nicname, String profile) {
        this.id = comment.getId();
        this.centent = comment.getComment();
        this.nicname = nicname;
        this.profile = profile;

    }
}
