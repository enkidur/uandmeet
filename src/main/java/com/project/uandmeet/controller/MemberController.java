package com.project.uandmeet.controller;


import com.project.uandmeet.dto.request.*;
import com.project.uandmeet.security.UserDetailsImpl;
import com.project.uandmeet.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class MemberController extends SavedRequestAwareAuthenticationSuccessHandler {
    
    private final MemberService memberService;

    // 회원가입(이메일 코드 없는 버전)
    @PostMapping("/api/users/signup")
    public ResponseEntity signup(@RequestBody SignupRequestDto signupRequestDto) {
        return memberService.signup(signupRequestDto);
    }

    // 비밀번호 찾기 (변경)
    @PatchMapping("/api/passwordChange")
    public ResponseEntity findPasswordChange(@RequestBody FindPasswordRequestDto findPasswordRequestDto) {
        return memberService.findPasswordChange(findPasswordRequestDto);
    }

    //닉네임중복체크
    @PostMapping("/api/users/nickname")
    public ResponseEntity nicknameCheck(@RequestBody NicknameRequestDto nicknameRequestDto) {
        return memberService.nicknameCheck(nicknameRequestDto);
    }

    //회원가입, 마이페이지 닉네임 수정
    @PatchMapping("/api/users/nickname/{memberId}")
    public ResponseEntity nickname(@PathVariable Long memberId,
                                   @RequestBody NicknameRequestDto nicknameRequestDto,
                                   final HttpServletResponse response) {
        return memberService.nickname(memberId, nicknameRequestDto, response);
    }

    //마이페이지 비밀번호 변경전 확인
    @PostMapping("/api/users/passwordCheck")
    public ResponseEntity passwordCheck(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody PasswordCheckRequestDto passwordCheckRequestDto) {
        return memberService.passwordCheck(userDetails, passwordCheckRequestDto);
    }

    //마이페이지에서 비밀번호 변경
    @PatchMapping("/api/users/passwordChange")
    public ResponseEntity passwordChange(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody PasswordRequestDto passwordRequestDto) {
        return memberService.passwordChange(userDetails, passwordRequestDto);
    }
    
    // TODO: 2022-08-26  마이페이지 프로필 사진변경 구현 필요
    // TODO: 2022-08-26  소셜로그인 구현필요 
    
    
}
