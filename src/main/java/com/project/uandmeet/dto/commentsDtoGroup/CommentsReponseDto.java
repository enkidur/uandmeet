package com.project.uandmeet.dto.commentsDtoGroup;


import com.project.uandmeet.model.Comment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentsReponseDto {
    private Long id;
    private String centent;
    private String nicname;
    private String profile;

    public CommentsReponseDto(Comment comment, String nicname, String profile) {
        this.id = comment.getId();
        this.centent = comment.getComment();
    }
}
