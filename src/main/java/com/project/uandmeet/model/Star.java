package com.project.uandmeet.model;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Star {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private Double star;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
}
