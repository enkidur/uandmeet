package com.project.uandmeet.dto;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class ProfileEditRequestDto {
    private String profile;
    private MultipartFile data;
}
