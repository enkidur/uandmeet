package com.project.uandmeet.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.project.uandmeet.dto.MemberRequestDto;
import lombok.*;

import javax.persistence.*;
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

    @Column(nullable = false, unique = true)
    private String username;

    @Column
    private String realname;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column
    private String phone;

    @Column
    private String birth;

    @Column
    private String gender;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String social;

    @Column
    private String profileImgUrl;

    @Column
    private String concern;

    @Column
    private Double star;

    @JsonManagedReference
    @OneToMany(mappedBy = "member", orphanRemoval = true)
    private List<Board> boards;

    @Column
    @Enumerated(value = EnumType.STRING) // Enum type을 STring 으로 변화하여 저장
    private MemberRoleEnum role;

    private String refreshToken;

    // kakaoUser
    // 일반 사용자가 로그인할 때 비워두기 위해 nullable = true,해당 아이디로 중복 가입이 되지않게 unique = true
    @Column(nullable = false, unique = true)
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

    public void setPassword(String encode) {
    }
}
