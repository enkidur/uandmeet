package com.project.uandmeet.dto;

import lombok.Getter;

    @Getter
    public class KakaoUserInfoDto {
        private String id;
        private String nickname;
        private String email;
        private String profile;
        private String gender;

        public KakaoUserInfoDto(String nickname, String email, String gender) {
            this.nickname = nickname;
            this.email =email;
            this.gender = gender;
        }
}
