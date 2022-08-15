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
public class InvitedMembers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @Column
    private Long postId;
    @JoinColumn(name="MEMBER_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Column
    private Boolean readCheck;  //조회여부

    @Column
    private LocalDateTime readCheckTime; //조회시간

    public InvitedMembers(Long postId, Member member) {
        this.postId = postId;
        this.member = member;
        this.readCheck =true;
    }

}
