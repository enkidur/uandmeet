package com.project.uandmeet.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Entity
@RequiredArgsConstructor
public class Review extends BaseTime{
/*
* 리뷰가 회원 리뷰만 들어가는건지?
* 매칭이 끝난후 그 매칭 게시물에 리뷰가 들어가는건지?
* */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "board")
//    private Board board;
    @Column(name = "board")
    private Long boardId;

    @Column(name = "form_member")
    private Long from;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "to_member")
//    private Member to;

    @Column(name = "to_member")
    private Long to;

    //리뷰 메시지
    @Column(nullable = false)
    private String message;

    //단일 평가점수
    @Column
    private Long evaluation_items;

    // 별점
    @Column
    @ElementCollection(fetch = FetchType.LAZY)
    private List<Integer> num;


    public Review(Long boardId , Long from, Long to, List<Integer> num, Long score, String review) {
        this.boardId = boardId;
        this.from = from;
        this.to = to;
        this.num = num;
        evaluation_items = score;
        message = review;
    }
}
