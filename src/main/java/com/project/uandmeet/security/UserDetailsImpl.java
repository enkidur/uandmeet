package com.project.uandmeet.security;


import com.project.uandmeet.model.Member;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

// 시큐리티가 /login 을 낚아채서 로그인을 진행함
// 로그인 진행이 완료되면 시큐리티 session 을 만듦 (Security ContextHolder 라는 key Value 로 session 정보 저장)
// Security session 오브젝트 타입은 Authentication 타입 객채
// Authentication 안에는 user 정보
// USer 오브젝트 타입은 UserDetails 타입 객체
// 즉, Security Session => Authentication => UserDetails(PrincipalDetails)
// -> PrincipalDetails 을 UserDetails 로 implements 하여 타입을 변경하여 Authentication 객체 안에 넣어줄 수 있다

@Getter
@Setter
public class UserDetailsImpl implements UserDetails, OAuth2User {

    private Member member; // 컴포지션
//    private KakaoMember kakaoMember;
    private Map<String,Object> attributes; // OAuth2

    public UserDetailsImpl(Member member) {
        this.member = member;
    }


    // Oauth2 오버라이드 메서드
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    // Oauth2 오버라이드 메서드
    @Override
    public String getName() {
        return null;
    }

    // 해당 User 의 권한의 리턴
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // member.getRole()을 Collection 타입으로 받기위해
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return String.valueOf(member.getRole());
            }
        });
//        Collection<GrantedAuthority> collection = new ArrayList<>();
//        member.getRole().forEach(r-> { // r : return
//            collection.add(()-> String.valueOf(r));
//        });
        return collection;
        // 권한이 존재안할 시
//        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getUsername();
    }

//    public String getEmail() {return member.getEmail();}

    // 계정이 만료되었는지 않았는지(ture : 만료 X, false : 만료)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정의 잠지 않았는지(ture : 잠김 X, false : 잠김)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 계정의 비밀번호 기간이 지났는지 않았는지(ture : 지남 X, false : 지남)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정의 활성화 여부(ture : 활성화, false : 활성화 X)
    @Override
    public boolean isEnabled() {
        // 1년 미로그인 고객 비활성화
        // 현재시간 - user.getLoginDate() => 1년
        // return false
        return true;
    }


}