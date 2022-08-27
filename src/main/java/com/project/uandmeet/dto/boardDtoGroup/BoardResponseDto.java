package com.project.uandmeet.dto.boardDtoGroup;

import com.project.uandmeet.dto.MemberDtoGroup.MemberSimpleDto;
import com.project.uandmeet.model.Board;
import lombok.*;

import java.net.PortUnreachableException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class BoardResponseDto {
    private MemberSimpleDto memberSimpleDto;
    private Long id;
    private LocalDateTime createdAt;
    private String category;
    private String title;
    private String boardimage;
    private Long likeCount;
    private Long commentCount;

    private String city;
    private String gu;

    private Long maxEntry;
    private Long currentEntry;
    private double lat;
    private double lng;

    public BoardResponseDto(MemberSimpleDto memberSimpleDto ,Board board)
    {
        this.memberSimpleDto = memberSimpleDto;
        this.city = board.getCity();
        this.gu = board.getGu();
        this.id = board.getId();
        this.createdAt = board.getCreatedAt();
        this.category = board.getCentent();
        this.title = board.getTitle();
        this.boardimage = board.getBoardimage();
        this.likeCount = board.getLikeCount();
        this.commentCount = board.getCommentCount();
        this.maxEntry = board.getMaxEntry();
        this.currentEntry = board.getCurrentEntry();
    }
}
