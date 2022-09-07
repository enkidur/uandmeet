package com.project.uandmeet.model;

import com.project.uandmeet.chat.model.ChatRoom;
import com.project.uandmeet.dto.boardDtoGroup.BoardRequestDto;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
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
    private String lat;
    //위도
    private String lng;

    private String boardimage;

    //매칭참여 MAX수
    @Column(nullable = false)
    private Long maxEntry=0L;

    //매칭참여 수
    @Column(nullable = false)
    private Long currentEntry=0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Siarea_id")
    private Siarea city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Guarea_id")
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

    @OneToOne
    @JoinColumn(name = "chatroom_id")
    private ChatRoom chatRoom;

    public Board(Member member, Category category, Siarea siarea, Guarea gu, BoardRequestDto.createAndCheck boardRequestDto, String uploadImage)
    {
        this.member = member;
        this.category = category;
        this.boardType = boardRequestDto.getBoardType();
        this.title = boardRequestDto.getTitle();
        this.content = boardRequestDto.getContent();
        this.boardimage = uploadImage;
        this.endDateAt =boardRequestDto.getEndDateAt();
        this.city = siarea;
        this.gu = gu;
        this.lat = boardRequestDto.getLat();
        this.lng = boardRequestDto.getLng();
        this.maxEntry = boardRequestDto.getMaxEntry();
    }

    public Board(Member member, Category category,Siarea siarea,Guarea gu, BoardRequestDto.createAndCheck boardRequestDto)
    {
        this.member = member;
        this.category = category;
        this.boardType = boardRequestDto.getBoardType();
        this.title = boardRequestDto.getTitle();
        this.content = boardRequestDto.getContent();
        this.endDateAt =boardRequestDto.getEndDateAt();
        this.city = siarea;
        this.gu = gu;
        this.lat = boardRequestDto.getLat();
        this.lng = boardRequestDto.getLng();
        this.maxEntry = boardRequestDto.getMaxEntry();
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
