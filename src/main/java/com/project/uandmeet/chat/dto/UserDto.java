package com.project.uandmeet.chat.dto;

import com.project.uandmeet.model.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    private String username;
    private String password;
    private String nickName;
    private String profileImgUrl;

    public UserDto(Member member) {
        this.username = member.getUsername();
        this.password = member.getPassword();
        this.nickName = member.getNickname();
        this.profileImgUrl = member.getProfileImgUrl();
    }
}
