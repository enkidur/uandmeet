package com.project.uandmeet.dto;

import com.project.uandmeet.model.Guarea;
import com.project.uandmeet.model.Siarea;
import lombok.Getter;

import java.util.List;

@Getter
public class MyListResponseDto {
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
    private Siarea city;
    private Guarea gu;
    //경도
    private String lat;
    //위도
    private String lng;
    private String boardimage;
    //매칭참여 MAX수
    private Long maxEntry;
    //매칭참여 수
    private Long currentEntry;
    private MyListMemberResponseDto writer;

    public MyListResponseDto(Long boardId, String boardType, String category, String title, String content, String endDateAt, Long likeCount, Long viewCount, Long commentCount, Siarea city, Guarea gu,String lat, String lng, String boardimage, Long maxEntry, Long currentEntry, MyListMemberResponseDto writer) {
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
        this.writer = writer;
    }
}