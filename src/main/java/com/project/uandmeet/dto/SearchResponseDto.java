package com.project.uandmeet.dto;

import com.project.uandmeet.model.Board;
import com.project.uandmeet.model.Category;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SearchResponseDto {

    private String boardType;
    private Category category;
    private Long id;
    private LocalDateTime createdAt;
    private String title;
    private String centent;
    private String boardimage;
    private LocalDateTime endDateAt;
    private String city;
    private String gu;

    private Long likeCount;
    private Long commentCount;

    private Long maxEntry;
    private Long currentEntry;

    //경도
    private double lat;
    //위도
    private double lng;

    public SearchResponseDto(Board board){
        this.city = board.getCity().getCtpKorNmAbbreviation();
        this.gu = board.getGu().getSigKorNm();
        this.id = board.getId();
        this.createdAt = board.getCreatedAt();
        this.category = board.getCategory();
        this.title = board.getTitle();
        this.boardimage = board.getBoardimage();
        this.likeCount = board.getLikeCount();
        this.commentCount = board.getCommentCount();
        this.maxEntry = board.getMaxEntry();
        this.currentEntry = board.getCurrentEntry();

    }

}