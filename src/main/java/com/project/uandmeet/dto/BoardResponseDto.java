package com.project.uandmeet.dto;

import lombok.Data;

@Data
public class BoardResponseDto {

    private String title;

    private String content;

    public BoardResponseDto(String title, String content){
        this.title = title;
        this.content = content;
    }

}
