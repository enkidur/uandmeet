package com.project.uandmeet.dto;

import lombok.Getter;

@Getter
public class MyListEntryResponseDto {
    private Long boardId;
    //정보공유 : information,  매칭:matching 둘중 하나.
    private String boardType;
    private String category;
    private String title;
    private String content;
    private String endDateAt;
    //좋아요 수
    private Long likeCount;
    //조회 수
    private Long viewCount;
    //댓글 수
    private Long commentCount;
    private String city;
    private String gu;
    //경도
    private String lat;
    //위도
    private String lng;
    private String boardimage;
    //매칭참여 MAX수
    private Long maxEntry;
    //매칭참여 수
    private Long currentEntry;
    private String createdAt;
    private String modifiedAt;
    private boolean reviewEntry;
    private MyListMemberResponseDto writer;

    public MyListEntryResponseDto(Long boardId, String boardType, String category, String title, String content, String endDateAt, Long likeCount, Long viewCount, Long commentCount, String city, String gu, String lat, String lng, String boardimage, Long maxEntry, Long currentEntry, String createdAt, String modifiedAt, boolean reviewEntry, MyListMemberResponseDto writer) {
        this.boardId = boardId;
        this.boardType = boardType;
        this.category = category;
        this.title = title;
        this.content = content;
        this.endDateAt = endDateAt;
        this.likeCount = likeCount;
        this.viewCount = viewCount;
        this.commentCount = commentCount;
        this.city = city;
        this.gu = gu;
        this.lat = lat;
        this.lng = lng;
        this.boardimage = boardimage;
        this.maxEntry = maxEntry;
        this.currentEntry = currentEntry;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.reviewEntry = reviewEntry;
        this.writer = writer;
    }
}
