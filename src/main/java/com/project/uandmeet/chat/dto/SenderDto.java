package com.project.uandmeet.chat.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SenderDto {
    private Long id;
    private String profile;
//    private String role;
    private String nickname;
}
