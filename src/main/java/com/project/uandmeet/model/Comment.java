package com.project.uandmeet.model;

import com.project.uandmeet.dto.commentsDtoGroup.CommentsRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //글내용
    private String comment;

    //테이블타입 정보공유 : information,  매칭:matching
    private String boardType;

    //생성시간
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    public Comment(CommentsRequestDto commentsRequestDto, Member member, Board board) {
        this.comment = commentsRequestDto.getContent();

    }
}
