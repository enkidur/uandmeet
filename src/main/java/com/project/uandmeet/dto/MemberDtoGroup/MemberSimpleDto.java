package com.project.uandmeet.dto.MemberDtoGroup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class MemberSimpleDto{
    private String nickname;
    private String username;
    private String profile;

    public MemberSimpleDto(String nickname,String username, String profile)
    {
        this.nickname = nickname;
        this.profile = profile;
        this.username = username;
    }

}
