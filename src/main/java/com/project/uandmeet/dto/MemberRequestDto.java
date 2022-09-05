package com.project.uandmeet.dto;

import com.project.uandmeet.model.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
public class MemberRequestDto {
    private String authNum;
    private String username;
    private String password;
    private String passwordCheck;
//    private MultipartFile userProfileImage;

    public Member register() {
        return new Member(username, password);
    }
}
