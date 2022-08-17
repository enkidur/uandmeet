package com.project.uandmeet.dto;

import lombok.Getter;

@Getter
public class LikeDto {
    private Long postid;
    private Long userid;

    public LikeDto(Long postid, Long userid) {
        this.postid = postid;
        this.userid = userid;
    }
}
