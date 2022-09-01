package com.project.uandmeet.model;

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
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String username;

    @Column(nullable = true, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column
    private String phone;

    @Column
    private String birth;

    @Column
    private String gender;

    @Column(nullable = true, unique = true)
    private String email;

    @Column
    private String profile; // 이미지

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<Concern> concern;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<JoinCnt> joinCnt;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "member")
    private List<Star> star;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "member",cascade = CascadeType.ALL)
    private List<Board> boardList = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "member",cascade = CascadeType.ALL)
    private List<Liked> likeList = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "member",cascade = CascadeType.ALL)
    private List<Entry> entryList = new ArrayList<>();

    @Column
    @Enumerated(value = EnumType.STRING) // Enum type을 STring 으로 변화하여 저장
    private MemberRoleEnum role;

    @Column
    private String loginto;

//    private String refreshToken;

    // 일반 사용자
//    public Member(String nickname, String encodedPassword, String email, String username) {
//        this.nickname = nickname;
//        this.password = encodedPassword;
//        this.email = email;
//        this.username = username;
//        this.kakoId = null;
//    }

    // kakaoUser
    @Builder
    public Member(String nickname, String encodedPassword, String email) {
        this.nickname = nickname;
        this.password = encodedPassword;
        this.username = email;
    }


//    public void updateRefreshToken(String newToken) {
//        this.refreshToken = newToken;
//    }

    public Member(String email, String password){
        this.username = email;
        this.password = password;
    }

    public Member(String loginto) {
        this.loginto = loginto;
    }
}
