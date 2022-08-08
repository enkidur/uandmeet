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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "board",fetch = FetchType.LAZY,cascade = CascadeType.ALL)//mappedBy 연관관계의 주인이 아니다(나는 FK가 아니에요) DB에 컬럼 만들지 마세요.
    private List<Like> likes = new ArrayList<>();


}
