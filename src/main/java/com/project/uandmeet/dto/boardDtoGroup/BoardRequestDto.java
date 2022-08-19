package com.project.uandmeet.dto.boardDtoGroup;

import lombok.*;

import java.time.LocalDateTime;


public class BoardRequestDto {

    //정보공유 : information,  매칭:matching 둘중 하나.
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class createAndCheck {

        private String boardType;
        private String category;
        private String title;
        private String centent;
        private String boardimage;
        private LocalDateTime endDateAt;
        private String city;
        private String gu;

        //경도
        private double lat;
        //위도
        private double lng;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class update {

        private String title;
        private String centent;
        private String boardimage;
        private String city;
        private String gu;
        private double lat;
        private double lng;
    }


}
