package com.project.uandmeet.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageDto {
    private String imageUrl;
    private String fileName;

    public ImageDto(String imageUrl, String fileName) {
        this.imageUrl = imageUrl;
        this.fileName = fileName;
    }
}
