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
}
