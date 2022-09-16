package com.project.uandmeet.model;

import com.project.uandmeet.dto.boardDtoGroup.EntryDto;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Entry extends BaseTime{

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
    private boolean isMatching;

    public Entry(Board board, Member member, EntryDto.request request) {
        this.member = member;
        this.board = board;
        this.category = board.getCategory();
        this.isMatching = request.getIsMatching();
    }
}