package com.project.uandmeet.model;

import com.project.uandmeet.dto.boardDtoGroup.BoardRequestDto;
import lombok.*;

import javax.persistence.*;
import java.text.DateFormat;
import java.time.LocalDateTime;
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
    private String content;
    //좋아요 수
    private Long likeCount;
    //조회 수
    private Long viewCount;
    //댓글 수
    private Long commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private String city;
    private String gu;
    //경도
    private double lat;
    //위도
    private double lng;
    private String boardimage;

    private Long maxEntry;

    private Long currentEntry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "board",cascade = CascadeType.ALL)
    private List<Entry> entryList = new ArrayList<>();

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "board",cascade = CascadeType.ALL)
    private List<Like> likeList = new ArrayList<>();

    public Board(Member member, Category category, BoardRequestDto boardRequestDto)
    {
        this.member = member;
        this.category = category;
        this.boardType = boardRequestDto.getBoardType();
        this.title = boardRequestDto.getTitle();
        this.content = boardRequestDto.getContent();
        this.boardimage = boardRequestDto.getBoardimage();
        this.city = boardRequestDto.getCity();
        this.gu = boardRequestDto.getGu();
        this.lat = boardRequestDto.getLat();
        this.lng = boardRequestDto.getLng();
    }

}
