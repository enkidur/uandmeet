package com.project.uandmeet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.project.uandmeet.dto.MemberRequestDto;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    private String social;

    @Column

    private String profileImgUrl;


    @Column
    private String concern;

    @Column
    private Double star;


    @OneToMany(fetch = FetchType.LAZY,mappedBy = "member",cascade = CascadeType.ALL)
    private List<Board> boardList = new ArrayList<>();


    @OneToMany(fetch = FetchType.LAZY,mappedBy = "member",cascade = CascadeType.ALL)
    private List<Like> likeList = new ArrayList<>();


    @OneToMany(fetch = FetchType.LAZY,mappedBy = "member",cascade = CascadeType.ALL)
    private List<Entry> entryList = new ArrayList<>();

    @Column
    @Enumerated(value = EnumType.STRING) // Enum type을 STring 으로 변화하여 저장
    private MemberRoleEnum role;

    @JsonIgnore
    private String refreshToken;

    // kakaoUser
    // 일반 사용자가 로그인할 때 비워두기 위해 nullable = true,해당 아이디로 중복 가입이 되지않게 unique = true
    @Column(nullable = true, unique = true)
    @JsonIgnore
    private String kakoId;

    // 일반 사용자
    public Member(String nickname, String encodedPassword, String email, String username) {
        this.nickname = nickname;
        this.password = encodedPassword;
        this.email = email;
        this.username = username;
        this.kakoId = null;
    }

    // kakaoUser
    public Member(String nickname, String encodedPassword, String email, String username, String KakaoId) {
        this.nickname = nickname;
        this.password = encodedPassword;
        this.email = email;
        this.username = username;
        this.kakoId = KakaoId;
    }


    public void updateRefreshToken(String newToken) {
        this.refreshToken = newToken;
    }

    public Member(String username, String password){
        this.username = username;
        this.password = password;
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

//    public void setPassword(String encode) {
//    }
}
