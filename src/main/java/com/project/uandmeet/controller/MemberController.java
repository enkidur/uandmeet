package com.project.uandmeet.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.uandmeet.auth.UserDetailsImpl;
import com.project.uandmeet.dto.CheckAuthNumDto;
import com.project.uandmeet.dto.EmailDto;
import com.project.uandmeet.dto.MemberRequestDto;
import com.project.uandmeet.service.EmailService;
import com.project.uandmeet.service.KakaoService;
import com.project.uandmeet.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.user.OAuth2User;
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

    // authenticatino으로 받아올 수도 있고, @AuthenticationPrincipal으로 받아올 수 도있음
    // authenticatino으로 받아올때는 OAuth2User로 다운캐스팅을 해서  getPrincipal()로 받아와야하고
    // @AuthenticationPrincipal 으로 받아올때는 getAttuributes()로 정보를 받아올수있음

//    시큐리티 세션안에는 Authentication이라는 객체가 있음
//    이 객체는 UserDetilas 또는 OAuth2User를 담을수있는데
//    컨트롤러에서 @AuthenticationPrincipal로 가져올수있는건 하나니까
//    둘 모두를 가져올 방법이 필요하다.
//    따라서 UserDetails에서 둘다 상속하게 한다.
    @GetMapping("/test/login")
    public @ResponseBody String loginTest(Authentication authentication,
                                          @AuthenticationPrincipal OAuth2User oauth){ // 세션값을 넘겨줌 DI(의존성 주입)
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        System.out.println("authentication: "+oAuth2User.getAttributes());
        System.out.println("oauth2User: "+oauth.getAttributes());

        return "OAuth 세션 정보 확인하기";
    }

    // OAuth 로그인을 해도 UserDetailsImpl
    // 일반 로그인을 해도 UserDetailsImpl
    @GetMapping("/user")
    public @ResponseBody String loginTest(@AuthenticationPrincipal UserDetailsImpl userDetails){
        System.out.println("유저디테일"+userDetails);
        System.out.println("principalDetails: "+userDetails.getMember());
        return "user";
    }


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


    @GetMapping("/api/kakaologin")
    public ResponseEntity<String> kakaoLogin(@RequestParam String code) throws JsonProcessingException {
        // authorizedCode: 카카오 서버로부터 받은 인가 코드
        kakaoService.kakaoLogin(code);
        return  ResponseEntity.ok("redirect:/login");
    }

}
