package com.project.uandmeet.dto;

import com.project.uandmeet.model.Board;
import com.project.uandmeet.model.Member;
import lombok.Getter;


@Getter
public class ReviewDto {


    private Long board;


    private Long from;


    private Long toId;
    private String toNickname;

    //리뷰 메시지
    private String message;

    //단일 평가점수
    private Long evaluation_items;

    private int num;

    public ReviewDto(Long board, Long from, Long boardUserId, String toNickname, int num, Long score, String review) {
        this.board = board;
        this.from = from;
        this.toId = boardUserId;
        this.toNickname = toNickname;
        this.num = num;
        this.evaluation_items = score;
        this.message = review;

    }
}
