package com.project.uandmeet.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Review {
    /*
     * 리뷰가 회원 리뷰만 들어가는건지?
     * 매칭이 끝난후 그 매칭 게시물에 리뷰가 들어가는건지?
     * */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member")
    private Member member;

    //리뷰 메시지
    @Column(nullable = false)
    private String message;

    //단일 평가점수
    private Long evaluation_items;

    private Timestamp created_at;
}
