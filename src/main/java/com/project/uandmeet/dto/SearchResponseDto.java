package com.project.uandmeet.dto;

import lombok.Data;

@Data
public class SearchResponseDto {

    private String title;

    private String content;

    public SearchResponseDto(String title, String content){
        this.title = title;
        this.content = content;
    }

}
