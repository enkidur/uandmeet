package com.project.uandmeet.dto;

import com.project.uandmeet.model.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequestDto {
    private String username;
    private String password;
    private String passwordCheck;

    //홍산의 추가
    private String nickname;
    private String profileImgUrl;

    public Member toEntity() {
        return new Member(username, password);
    }
}
