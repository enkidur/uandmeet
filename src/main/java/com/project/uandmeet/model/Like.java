//package com.project.uandmeet.model;
//
//import com.project.uandmeet.dto.LikeDto;
//import lombok.*;
//
//import javax.persistence.*;
//
//@Getter
//@Setter
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//@Entity
//@Table(name = "Likes")
//public class Like {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "member_id")
//    private Member member;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "board")
//    private Board board;
//
//
//    public Like(Member memberid, Board boardid) {
//        this.member = memberid;
//        this.board = boardid;
//    }
//
//}
