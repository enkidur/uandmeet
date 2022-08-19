package com.project.uandmeet.dto;

import com.project.uandmeet.model.Concern;
import com.project.uandmeet.model.JoinCnt;
import lombok.Getter;

import java.util.List;

@Getter
public class MypageDto {
    private String nickname;
    private List<Concern> concern;
    private List<JoinCnt> joincnt;


    public MypageDto(String nickname, List<Concern> concern, List<JoinCnt> joinCnt) {
        this.nickname = nickname;
        this.concern = concern;
        this.joincnt = joinCnt;
    }
}
