package com.project.uandmeet.model;

import lombok.*;

import javax.persistence.*;

/**
 * 참여한 매칭들
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Entry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column
    private boolean ismatching;

    public Entry(Board board, Member member) {
        this.member = member;
        this.board = board;
    }

    public void changeIsMatching(boolean ismatching){
        this.ismatching = ismatching;
    }
}
