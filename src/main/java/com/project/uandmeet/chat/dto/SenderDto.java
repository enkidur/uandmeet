package com.project.uandmeet.chat.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SenderDto {
    private Long id;    //회원 고유번호
    private String profile; //프로필 사진
//    private String role;
    private String nickname;    //닉네임
}
