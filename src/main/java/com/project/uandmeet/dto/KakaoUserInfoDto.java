package com.project.uandmeet.dto;

import lombok.Getter;

    @Getter
    public class KakaoUserInfoDto {
        private String id;
        private String nickname;
        private String email;

        public KakaoUserInfoDto( String nickname, String email) {

            this.nickname = nickname;
            this.email =email;
        }
}
