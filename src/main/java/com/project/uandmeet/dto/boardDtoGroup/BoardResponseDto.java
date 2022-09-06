package com.project.uandmeet.dto.boardDtoGroup;

import com.project.uandmeet.dto.MemberDtoGroup.MemberSimpleDto;
import com.project.uandmeet.model.Board;
import com.project.uandmeet.model.Category;
import com.project.uandmeet.model.Liked;
import lombok.*;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
public class BoardResponseDto {
    private MemberSimpleDto memberSimpleDto;
    private Long id;
    private String title;
    private String category;
    private String createdAt;
    private String endDateAt;
    private Long maxEntry;
    private Long currentEntry;
    private String content;
    private String boardimage;
    private Long likeCount;
    private Long commentCount;
    private String city;
    private String gu;
    private double lat;
    private double lng;
    private boolean LikeState;
    public BoardResponseDto(MemberSimpleDto memberSimpleDto ,Board board)
    {
        this.memberSimpleDto = memberSimpleDto;
        this.city = board.getCity().getCtpKorNmAbbreviation();
        this.gu = board.getGu().getSigKorNm();
        this.id = board.getId();
        this.createdAt = board.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:SS:ss.SSS"));
        this.category = board.getCategory().getCategory();
        this.endDateAt = board.getEndDateAt();
        this.content = board.getContent();
        this.title = board.getTitle();
        this.boardimage = board.getBoardimage();
        this.likeCount = board.getLikeCount();
        this.commentCount = board.getCommentCount();
        this.maxEntry = board.getMaxEntry();
        this.currentEntry = board.getCurrentEntry();
    }
    public BoardResponseDto(Board board,MemberSimpleDto memberSimpleDto)
    {
        this.memberSimpleDto = memberSimpleDto;
        this.id = board.getId();
        this.createdAt = board.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:SS:ss.SSS"));
        this.category = board.getCategory().getCategory();
        this.endDateAt = board.getEndDateAt();
        this.content = board.getContent();
        this.title = board.getTitle();
        this.boardimage = board.getBoardimage();
        this.likeCount = board.getLikeCount();
        this.commentCount = board.getCommentCount();
    }

    public BoardResponseDto(MemberSimpleDto memberSimpleDto , Board board, Liked liked)
    {
        this.memberSimpleDto = memberSimpleDto;
        this.city = board.getCity().getCtpKorNmAbbreviation();
        this.gu = board.getGu().getSigKorNm();
        this.id = board.getId();
        this.createdAt = board.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:SS:ss.SSS"));
        this.category = board.getCategory().getCategory();
        this.endDateAt = board.getEndDateAt();
        this.content = board.getContent();
        this.title = board.getTitle();
        this.boardimage = board.getBoardimage();
        this.likeCount = board.getLikeCount();
        this.commentCount = board.getCommentCount();
        this.maxEntry = board.getMaxEntry();
        this.currentEntry = board.getCurrentEntry();
        this.LikeState = liked.getIsLike();
    }
}
