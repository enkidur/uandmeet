package com.project.uandmeet.model;

import lombok.*;

import javax.persistence.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //정보공유 : information,  매칭:matching 둘중 하나.
    private String boardType;

    private String title;

    private String centent;

    //좋아요 수
    private Long likeCount;

    //조회 수
    private Long viewCount;

    //댓글 수
    private Long commentCount;


    private Point location;

    //경도
    private double lat;

    //위도
    private double lng;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private int viewCount;

    private int likeCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    //
}
