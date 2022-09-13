package com.project.uandmeet.dto;

import com.project.uandmeet.model.Board;
import com.project.uandmeet.model.Category;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SearchResponseDto {

    private String boardType;
    private String category;
    private Long id;
    private LocalDateTime createdAt;
    private String title;
    private String content;
    private String boardimage;

    private LocalDateTime endDateAt;
    private String city;
    private String gu;

    private String profile;

    private String username;

    private String nickname; // 검색 결과에 해당 게시물을 누가 썻는지 표시하기 위한 닉네임

    private Long likeCount;
    private Long commentCount;

    private Long maxEntry;
    private Long currentEntry;

    //경도
    private double lat;
    //위도
    private double lng;

    // 매칭
    public SearchResponseDto(Board board){
        this.city = board.getCity().getCtpKorNmAbbreviation();
        this.gu = board.getGu().getSigKorNm();
        this.id = board.getId();
        this.createdAt = board.getCreatedAt();
        this.category = board.getCategory().getCategory();
        this.title = board.getTitle();
        this.boardimage = board.getBoardimage();
        this.likeCount = board.getLikeCount();
        this.commentCount = board.getCommentCount();
        this.maxEntry = board.getMaxEntry();
        this.currentEntry = board.getCurrentEntry();
        this.content=board.getContent();
        this.nickname=board.getMember().getNickname();
        this.profile=board.getMember().getProfile();
        this.username=board.getMember().getUsername();
        this.boardType=board.getBoardType();
    }

    //정보공유
    public SearchResponseDto(Board board,String bt){
        this.id = board.getId();
        this.createdAt = board.getCreatedAt();
        this.category = board.getCategory().getCategory();
        this.title = board.getTitle();
        this.boardimage = board.getBoardimage();
        this.likeCount = board.getLikeCount();
        this.commentCount = board.getCommentCount();
        this.maxEntry = board.getMaxEntry();
        this.currentEntry = board.getCurrentEntry();
        this.content=board.getContent();
        this.nickname=board.getMember().getNickname();
        this.profile=board.getMember().getProfile();
        this.username=board.getMember().getUsername();
        this.boardType=bt;
    }

}