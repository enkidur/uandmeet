package com.project.uandmeet.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
public class MypageDto {
    private String nickname;
    private List<String> concern;
    private Map<String, Long> joinCnt;


    public MypageDto(String nickname, List<String> concern, Map<String, Long> joinCnt) {
        this.nickname = nickname;
        this.concern = concern;
        this.joinCnt = joinCnt;
    }
    public MypageDto(String nickname, List<String> concern) {
        this.nickname = nickname;
        this.concern = concern;
    }
}