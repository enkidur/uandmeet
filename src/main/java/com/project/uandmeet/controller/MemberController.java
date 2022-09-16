package com.project.uandmeet.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.uandmeet.dto.*;
import com.project.uandmeet.exception.CustomException;
import com.project.uandmeet.model.Review;
import com.project.uandmeet.security.UserDetailsImpl;
import com.project.uandmeet.service.EmailService;
import com.project.uandmeet.service.KakaoService;
import com.project.uandmeet.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final KakaoService kakaoService;
    private final EmailService emailService;


    // 회원가입 1. emali check + 인증번호 발송
    @PostMapping("/api/checkemail")
    public ResponseEntity<String> checkemail(@RequestBody EmailDto requestDto) {
        memberService.checkemail(requestDto.getUsername());
        return ResponseEntity.ok(emailService.joinEmail(requestDto.getUsername()));
    }

    // 회원가입 2. Email 인증번호 확인
    @PostMapping("/api/checkAuthNum")
    public @ResponseBody ResponseEntity<String> checkAuthNum(@RequestBody AuthNumDto requestDto) {
        return ResponseEntity.ok(emailService.checkAuthNum(requestDto.getAuthNum(), requestDto.getUsername()));
    }

    // 회원가입 3. password check + 회원가입
    @PostMapping("/api/checkpassword")
    public ResponseEntity<String> checkPassword(@RequestBody MemberRequestDto requestDto) {
        memberService.checkPassword(requestDto.getPassword(), requestDto.getPasswordCheck());
        return ResponseEntity.ok(memberService.signup(requestDto));
    }

    // 회원가입 test
    @PostMapping("/api/join")
    public void join(@RequestBody MemberRequestDto requestDto) {
        memberService.join(requestDto);

    }

    // logout
    @GetMapping("/api/logout")
    public void logout (@AuthenticationPrincipal UserDetailsImpl userDetails) {
        memberService.logout(userDetails);
    }

    // 회원 탈퇴
    @PostMapping("/api/withdraw")
    public ResponseEntity<String> withdraw(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                           @RequestBody PasswordDto requestDto) {
        return memberService.withdraw(userDetails, requestDto.getPassword());
    }

    // 1. 클라이언트에서 로그인한다.
    // 2. 서버는 클라이언트에게 Access Token 과 Refresh Token 을 발급한다. 동시에 Refresh Token 은 redeis 에 저장된다.
    // 3. 클라이언트는 local 저장소에 Token 을 저장한다.
    // 4. 매 요청마다 Access Token 을 헤더에 담아서 요청한다.
    // 5 .이 때, Access Token 이 만료가 되면 서버는 만료되었다는 Response 를 하게 된다.
    // 6. 클라이언트는 해당 Response 를 받으면 Refresh Token 을 보낸다.
    // 7. 서버는 Refresh Token 유효성 체크를 하게 되고, 새로운 Access Token 을 발급한다.
    // 8. 클라이언트는 새롭게 받은 Access Token 을 기존의 Access Token 에 덮어쓰게 된다.
    @GetMapping("/api/refresh")
    public ResponseEntity<String> refresh(HttpServletRequest request,
                                                       HttpServletResponse response) throws IOException {
        return ResponseEntity.ok(memberService.refresh(request, response));
    }

    //  password 찾기 1. password 찾기 -> 인증번호 발송
    @PostMapping("/api/findPassword")
    public ResponseEntity<String> findpassword(@RequestBody EmailDto requestDto) {
        return ResponseEntity.ok(memberService.findpassword(requestDto.getUsername()));
    }
    // 인증 번호로 인증 받고 차후 비밀번호 변경

    // password 찾기 2. password 인증번호 확인
    @PostMapping("/api/findCheck")
    public ResponseEntity<String> findCheck(@RequestBody AuthNumDto requestDto) {
        return ResponseEntity.ok(memberService.findCheck(requestDto.getAuthNum(), requestDto.getUsername()));
    }

    //  password 찾기 3. password 변경
    @PostMapping("/api/passChange")
    public ResponseEntity<String> passChange(@RequestBody PasswordChangeDto passwordChangeDto) {
        return ResponseEntity.ok(memberService.passChange(passwordChangeDto));
    }

    // password 변경
    @PostMapping("/api/changepass")
    public ResponseEntity<String> changepass(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                             @RequestBody PasswordChangeDto passwordChangeDto) {
        return ResponseEntity.ok(memberService.changepass(userDetails, passwordChangeDto));
    }

    // kakao login
    @GetMapping("/user/kakao/callback")
    public ResponseEntity<Map<String, String>> kakaoLogin(@RequestParam String code) throws JsonProcessingException, UnsupportedEncodingException {
        // authorizedCode: 카카오 서버로부터 받은 인가 코드
        return ResponseEntity.ok(kakaoService.kakaoLogin(code));
    }
    @GetMapping("/api/test")
    public String test() {
        return "성공";
    }
}