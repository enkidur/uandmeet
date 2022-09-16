package com.project.uandmeet.dto.commentsDtoGroup;

import com.project.uandmeet.dto.MemberDtoGroup.MemberSimpleDto;
import com.project.uandmeet.model.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentsInquiryDto {
    private MemberSimpleDto writer;
    private Long id;
    private String createdAt;
    private String comment;

    public CommentsInquiryDto (MemberSimpleDto memberSimpleDto, Comment comment)
    {
        this.writer = memberSimpleDto;
        this.id = comment.getId();
        this.comment = comment.getComment();
        this.createdAt = comment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS"));
        System.out.println(createdAt);
        System.out.println(comment.getCreatedAt());
    }
}
