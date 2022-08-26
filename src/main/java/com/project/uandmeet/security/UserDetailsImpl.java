package com.project.uandmeet.security;

import com.project.uandmeet.dto.MemberRequestDto;
import com.project.uandmeet.model.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserDetailsImpl implements UserDetails {

    private final Member member;
    private String username;
    private String nickname;

    public UserDetailsImpl(Member member) {
        this.member = member;
    }

    public UserDetailsImpl() {
        this.member = null;
    }

    public Member getMember() {
        return member;
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getUsername();
    }

    @Override //계정의 만료여부 리턴 스프링시큐리티의 기능들
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override //계정의 잠금여부를 리턴
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override //계정의 비번이 만료되었는지 리턴
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override //사용가능한계정인지 리턴
    public boolean isEnabled() {
        return true;
    }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    // UserRequestDto로부터 UserDetailsImpl 생성
    public static UserDetailsImpl fromMemberRequestDto(MemberRequestDto requestDto){

        UserDetailsImpl userDetails = new UserDetailsImpl();

        userDetails.username = requestDto.getUsername();
        userDetails.nickname = requestDto.getNickname();


        return userDetails;
    }
}