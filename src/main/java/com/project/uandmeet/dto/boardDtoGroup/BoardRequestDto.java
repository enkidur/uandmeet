package com.project.uandmeet.dto.boardDtoGroup;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;


public class BoardRequestDto {
    //정보공유 : information,  매칭:matching 둘중 하나.

    @ToString
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class createAndCheck {

        private String boardType;
        private String category;
        private String title;
        private String content;
        private String boardimage;
        private String endDateAt;
        private String city;
        private String gu;
        private String maxEntry;
        private MultipartFile data;


        //경도
        private String lat;
        //위도
        private String lng;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class updateMatching {

        private String title;
        private String content;
        private String boardimage;
        private String endDateAt;
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class updateInfo {

        private String title;
        private String content;
        private String boardimage;
        private String endDateAt;
    }
}
