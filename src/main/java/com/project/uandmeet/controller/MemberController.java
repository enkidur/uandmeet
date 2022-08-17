package com.project.uandmeet.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.uandmeet.dto.CheckAuthNumDto;
import com.project.uandmeet.dto.EmailDto;
import com.project.uandmeet.dto.MemberRequestDto;
import com.project.uandmeet.service.EmailService;
import com.project.uandmeet.service.KakaoService;
import com.project.uandmeet.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

//@RestController
@Controller
@RequestMapping
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final KakaoService kakaoService;
    private final EmailService emailService;


    @PostMapping("/api/join")
    public ResponseEntity<String> join(@RequestBody MemberRequestDto requestDto) throws IOException {
        return ResponseEntity.ok(memberService.join(requestDto));
    }


    // 1. 클라이언트에서 로그인한다.
    // 2. 서버는 클라이언트에게 Access Token 과 Refresh Token 을 발급한다. 동시에 Refresh Token 은 redeis 에 저장된다.
    // 3. 클라이언트는 local 저장소에 두 Token 을 저장한다.
    // 4. 매 요청마다 Access Token 을 헤더에 담아서 요청한다.
    // 5 .이 때, Access Token 이 만료가 되면 서버는 만료되었다는 Response 를 하게 된다.
    // 6. 클라이언트는 해당 Response 를 받으면 Refresh Token 을 보낸다.
    // 7. 서버는 Refresh Token 유효성 체크를 하게 되고, 새로운 Access Token 을 발급한다.
    // 8. 클라이언트는 새롭게 받은 Access Token 을 기존의 Access Token 에 덮어쓰게 된다.
    @GetMapping("/api/refresh")
    public ResponseEntity<Map<String, String>> refresh(HttpServletRequest request, HttpServletResponse response) {
//        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
//
//        if (authorizationHeader == null || !authorizationHeader.startsWith(JwtProperties.TOKEN_PREFIX)) {
//            throw new RuntimeException("JWT Token이 존재하지 않습니다.");
//        }
//        String refreshToken = authorizationHeader.substring(JwtProperties.TOKEN_PREFIX.length());
//        Map<String, String> tokens = memberService.refresh(refreshToken);
//        response.setHeader(JwtProperties.HEADER_ACCESS, tokens.get(JwtProperties.HEADER_ACCESS));
//        if (tokens.get(JwtProperties.HEADER_REFRESH) != null) {
//            response.setHeader(JwtProperties.HEADER_REFRESH, tokens.get(JwtProperties.HEADER_REFRESH));
//        }
        Map<String, String> tokens = memberService.refresh(request, response);
        return ResponseEntity.ok(tokens);
    }


    // Email 인증번호 확인
    @PostMapping("/api/checkAuthNum")
    public @ResponseBody ResponseEntity<Boolean> checkAuthNum(@RequestBody CheckAuthNumDto checkAuthNumDto) {
        return ResponseEntity.ok(emailService.checkAuthNum(checkAuthNumDto));
    }

    // password 찾기
    @PostMapping("/api/findpassword")
    public ResponseEntity<String> findpassword(@RequestBody EmailDto requestDto) {
        memberService.findpassword(requestDto.getUsername());
        return ResponseEntity.ok("password 찾기 완료");
    }

    // login test
    @GetMapping("/api/loginForm")
    public String loginFrom() {
        return "login.html";
    }

    // Email 인증 test
    @PostMapping("/api/mailCheck")
    public @ResponseBody String mailCheck(@RequestBody EmailDto requestDto) {
        System.out.println("이메일 인증 요청이 들어옴!");
        System.out.println("이메일 인증 이메일 : " + requestDto.getUsername());
        return emailService.joinEmail(requestDto.getUsername());
    }

    // kakao

    @GetMapping("/api/kakaologin")
    public ResponseEntity<String> kakaoLogin(@RequestParam String code) throws JsonProcessingException {
        // authorizedCode: 카카오 서버로부터 받은 인가 코드
        kakaoService.kakaoLogin(code);
        return  ResponseEntity.ok("redirect:/login");
    }

}
