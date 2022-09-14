package com.project.uandmeet.dto;

import com.project.uandmeet.model.BaseTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;


@Getter
public class MainPageEntryDto {
    private String kind;
    private Long boardId;
    private String title;
    private Long maxEntry;
    private Long currentEntry;
    private Boolean ismathcing;
    private String boardimage;
    private String createdAt;
    private String modifiedAt;

    public MainPageEntryDto(String kind, Long boardId, String title, Long maxEntry, Long currentEntry, Boolean matching, String boardimage, String createdAt, String modifiedAt) {
        this.kind = kind;
        this.boardId = boardId;
        this.title = title;
        this.maxEntry = maxEntry;
        this.currentEntry = currentEntry;
        ismathcing = matching;
        this.boardimage = boardimage;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}
