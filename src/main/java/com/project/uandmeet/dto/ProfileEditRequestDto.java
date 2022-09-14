package com.project.uandmeet.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileEditRequestDto {
    private MultipartFile data;
}
