package com.project.uandmeet.dto.commentsDtoGroup;

import com.project.uandmeet.dto.MemberDtoGroup.MemberSimpleDto;
import com.project.uandmeet.model.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentsInquiryDto {
    private MemberSimpleDto memberSimpleDto;
    private Long id;
    private LocalDateTime createdAt;
    private String comment;

    public CommentsInquiryDto (MemberSimpleDto memberSimpleDto, Comment comment)
    {
        this.memberSimpleDto = memberSimpleDto;
        this.id = comment.getId();
        this.comment = comment.getComment();
        this.createdAt = comment.getCreatedAt();
    }
}
