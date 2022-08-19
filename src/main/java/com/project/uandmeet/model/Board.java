package com.project.uandmeet.model;

import com.project.uandmeet.dto.boardDtoGroup.BoardRequestDto;
import lombok.*;

import javax.persistence.*;
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
    private String centent;

    //좋아요 수
    @Column(nullable = false)
    private Long likeCount = 0L;
    //조회 수
    @Column(nullable = false)
    private Long viewCount = 0L;
    //댓글 수
    @Column(nullable = false)
    private Long commentCount = 0L;

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

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "board",cascade = CascadeType.ALL)
    private List<Entry> entryList = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "board",cascade = CascadeType.ALL)
    private List<Liked> likeList = new ArrayList<>();

    public Board(Member member, Category category, BoardRequestDto.createAndCheck boardRequestDto)
    {
        this.member = member;
        this.category = category;
        this.boardType = boardRequestDto.getBoardType();
        this.title = boardRequestDto.getTitle();
        this.centent = boardRequestDto.getCentent();
        this.boardimage = boardRequestDto.getBoardimage();
        this.city = boardRequestDto.getCity();
        this.gu = boardRequestDto.getGu();
        this.lat = boardRequestDto.getLat();
        this.lng = boardRequestDto.getLng();
    }

    public Board(Board board, BoardRequestDto.createAndCheck boardRequestUpdateDto)
    {
        this.member = board.getMember();
        this.category = board.getCategory();
        this.boardType = board.getBoardType();
        this.title = boardRequestUpdateDto.getTitle();
        this.centent = boardRequestUpdateDto.getCentent();
        this.boardimage = boardRequestUpdateDto.getBoardimage();
        this.city = boardRequestUpdateDto.getCity();
        this.gu = boardRequestUpdateDto.getGu();
        this.lat = boardRequestUpdateDto.getLat();
        this.lng = boardRequestUpdateDto.getLng();
    }


    public Board(Long likeCountadd, Board board) {
        this.id = board.getId();
        this.likeCount = likeCountadd;

    }
}
