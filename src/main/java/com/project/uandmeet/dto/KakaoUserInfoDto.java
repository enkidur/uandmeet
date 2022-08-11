package com.project.uandmeet.dto;

import lombok.Getter;

    @Getter
    public class KakaoUserInfoDto {
        private String username;
        private String nickname;
        private String email;

        public KakaoUserInfoDto(String  username, String nickname, String email) {
            this.username = username;
            this.nickname = nickname;
            this.email =email;
        }
}
