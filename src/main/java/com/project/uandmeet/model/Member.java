package com.project.uandmeet.model;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column
    private String realname;

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
    private String profileImgUrl;

    @Column
    private String concern;

    @Column
    private Double star;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "member",cascade = CascadeType.REMOVE)
    private List<Board> boardList = new ArrayList<>();

    @Column
    @Enumerated(value = EnumType.STRING) // Enum type을 STring 으로 변화하여 저장
    private MemberRoleEnum role;

    private String refreshToken;

    // kakaoUser
    // 일반 사용자가 로그인할 때 비워두기 위해 nullable = true,해당 아이디로 중복 가입이 되지않게 unique = true
    @Column(nullable = true, unique = true)
    private String kakoId;

    // 일반 사용자
    public Member(String username, String encodedPassword, String email) {
        this.password = encodedPassword;
        this.email = email;
        this.username = username;
        this.kakoId = null;
    }

    // kakaoUser
//    public Member(String nickname, String encodedPassword, String email, String username, String KakaoId) {
//        this.nickname = nickname;
//        this.password = encodedPassword;
//        this.email = email;
//        this.username = username;
//        this.kakoId = KakaoId;
//    }
    public Member(String nickname, String encodedPassword, String email, String username) {
        this.nickname = nickname;
        this.password = encodedPassword;
        this.email = email;
        this.username = username;

    }


    public void updateRefreshToken(String newToken) {
        this.refreshToken = newToken;
    }

    public Member(String username, String password){
        this.username = username;
        this.password = password;
    }
}
