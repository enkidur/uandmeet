package com.project.uandmeet.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Board extends Timestamped{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String photoUrl;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;


//    @OneToMany(mappedBy = "board", orphanRemoval = true)
//    private List<BoardLikes> boardLikes;

    public void minuslikecnt() {
        this.likeCount -= 1;
    }

    public void pluslikecnt() {
        this.likeCount += 1;
    }

}
