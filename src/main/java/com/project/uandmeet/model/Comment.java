package com.project.uandmeet.model;

import com.project.uandmeet.dto.CommentRequestDto;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "posts_id")
    private Board board;
    private String content;

    public Comment(CommentRequestDto requestDto, Member member, Board board) {
        this.content = requestDto.getContent();
        this.member = member;
        this.board = board;
    }
}
//requestsDto.getContent(),userDetails.getUsername(),board
