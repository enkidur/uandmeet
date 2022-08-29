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
        private MultipartFile data;


        //경도
        private double lat;
        //위도
        private double lng;

    }

    @Getter
    public static class updateMatching {

        private String title;
        private String content;
        private String boardimage;
        private String endDateAt;
    }
    @Getter
    public static class updateInfo {

        private String title;
        private String content;
        private String boardimage;
        private String endDateAt;
    }
}
