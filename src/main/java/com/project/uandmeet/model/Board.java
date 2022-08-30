package com.project.uandmeet.model;

import com.project.uandmeet.dto.boardDtoGroup.BoardRequestDto;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Board extends BaseTime{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //정보공유 : information,  매칭:matching 둘중 하나.
    private String boardType;
    private String title;
    private String content;
    private String endDateAt;

    //좋아요 수
    @Column(nullable = false)
    private Long likeCount = 0L;
    //조회 수
    @Column(nullable = false)
    private Long viewCount = 0L;
    //댓글 수
    @Column(nullable = false)
    private Long commentCount = 0L;

    //경도
    private double lat;
    //위도
    private double lng;

    private String boardimage;

    private Long maxEntry;

    private Long currentEntry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "siarea_id")
    private Siarea city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guarea_id")
    private Guarea gu;

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

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "board",cascade = CascadeType.ALL)
    private List<Comment> commentList = new ArrayList<>();

    public Board(Member member, Category category, BoardRequestDto.createAndCheck boardRequestDto, Siarea siarea, Guarea guarea)
    {
        this.member = member;
        this.category = category;
        this.city = siarea;
        this.gu = guarea;
        this.boardType = boardRequestDto.getBoardType();
        this.title = boardRequestDto.getTitle();
        this.content = boardRequestDto.getContent();
        this.boardimage = boardRequestDto.getBoardimage();
        this.endDateAt =boardRequestDto.getEndDateAt();
        this.lat = boardRequestDto.getLat();
        this.lng = boardRequestDto.getLng();
    }


    //매칭 업데이트
    public Board(Board board, BoardRequestDto.updateMatching boardRequestUpdateDto)
    {
        this.member = board.getMember();
        this.title = boardRequestUpdateDto.getTitle();
        this.content = boardRequestUpdateDto.getContent();
        this.boardimage = boardRequestUpdateDto.getBoardimage();
        this.endDateAt = boardRequestUpdateDto.getEndDateAt();
    }

    //공유 업데이트
    public Board(Board board, BoardRequestDto.updateInfo boardRequestUpdateDto)
    {
        this.member = board.getMember();
        this.title = boardRequestUpdateDto.getTitle();
        this.content = boardRequestUpdateDto.getContent();
        this.boardimage = boardRequestUpdateDto.getBoardimage();
    }
}
