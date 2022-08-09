package com.project.uandmeet.dto.boardDtoGroup;

import com.project.uandmeet.model.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardRequestDto {
    //정보공유 : information,  매칭:matching 둘중 하나.
    private String boardType;
    private String category;
    private String title;
    private String centent;
    private String boardimage;
    private String city;
    private String gu;

    //경도
    private double lat;
    //위도
    private double lng;
}
