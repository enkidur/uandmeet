package com.project.uandmeet.model;

import com.project.uandmeet.dto.MemberDtoGroup.MemberSimpleDto;
import com.project.uandmeet.dto.commentsDtoGroup.CommentsRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends BaseTime{



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //글내용
    private String comment;

    //테이블타입 정보공유 : information,  매칭:matching
    private String boardType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    public Comment(CommentsRequestDto commentsRequestDto,Member member, Board board) {
        this.comment = commentsRequestDto.getContent();
        this.member = member;
        this.board = board;
        this.boardType = board.getBoardType();
    }
}
