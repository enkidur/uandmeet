package com.project.uandmeet.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.project.uandmeet.dto.MemberRequestDto;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String username;
    @Column(nullable = true, unique = true)
    private String nickname;
    @Column(nullable = true)
    private String password;

    @Column
    private String phone;

    @Column
    private String birth;

    @Column
    private boolean gender; // true 남성, flase 여성

    @Column(nullable = true, unique = true)
    private String email;

    @Column(nullable = false)
    private String social;

    @Column
    private String profileImgUrl;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<Concern> concern;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<JoinCnt> joinCnt;

    @Column
    private Double star;

    @JsonManagedReference
    @OneToMany(mappedBy = "member", orphanRemoval = true)
    private List<Board> boards;

    @Column
    @Enumerated(value = EnumType.STRING) // Enum type을 STring 으로 변화하여 저장
    private MemberRoleEnum role;

    @Column
    private String loginto;

//    private String refreshToken;


    // kakaoUser
    @Builder
    public Member(String nickname, String encodedPassword, String email) {
        this.nickname = nickname;
        this.password = encodedPassword;
        this.username = email;
    }

    // googleUser
    @Builder
    public Member(String email, String loginto){
        this.email = email;
        this.loginto = loginto;
//        this.role = role;
    }

    public Member(String loginto) {
        this.loginto = loginto;
    }

    //홍산의 추가 (삭제?)

    public Member(String realname, String nickname,String email, String encodedPassword,String profileImgUrl, String social) {
        this.realname = realname;
        this.nickname = nickname;
        this.email = email;
        this.password = encodedPassword;
        this.profileImgUrl = profileImgUrl;
        this.social = social;
    }

    public void updateNickname(String nickname){
        this.nickname = nickname;
    }

    // 마이페이지 업데이트
    public void updateUser(Long userId, MemberRequestDto memberRequestDto) {
        this.id = userId;
        this.nickname = memberRequestDto.getNickname();
        this.profileImgUrl = memberRequestDto.getProfileImgUrl();
    }

    public void updateProfileImg(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

    public void setPassword(String encode) {
    }
}
