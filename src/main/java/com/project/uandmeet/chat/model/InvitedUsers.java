package com.project.uandmeet.chat.model;

import com.project.uandmeet.model.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class InvitedUsers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @Column
    private Long boardId;
    @JoinColumn(name="MEMBER_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Column
    private Boolean readCheck;
    @Column
    private LocalDateTime readCheckTime;

    public InvitedUsers(Long boardId, Member member) {
        this.boardId = boardId;
        this.member = member;
        this.readCheck =true;
    }

}
