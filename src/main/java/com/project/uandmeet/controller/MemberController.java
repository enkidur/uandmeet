package com.project.uandmeet.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.uandmeet.auth.UserDetailsImpl;
import com.project.uandmeet.dto.*;
import com.project.uandmeet.model.Concern;
import com.project.uandmeet.redis.RedisUtil;
import com.project.uandmeet.service.EmailService;
import com.project.uandmeet.service.KakaoService;
import com.project.uandmeet.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

//@RestController
@Controller
@RequestMapping
@RequiredArgsConstructor
public class MemberController {
    private final RedisUtil redisUtil;
    private final MemberService memberService;
    private final KakaoService kakaoService;
    private final EmailService emailService;

    // 회원가입 1. emali check
    @PostMapping("/api/checkemail")
    public ResponseEntity<String> checkemail(@RequestBody String username) throws IOException {
        // 헤더에 전달
//        headers.set("level","1");
//        ResponseEntity<String> res = ResponseEntity.ok()
//                                     .headers(headers)
//                                     .body(memberService.checkemail(username));
        // redis 에 저장
        ResponseEntity<String> res = ResponseEntity.ok(memberService.checkemail(username));
            redisUtil.setDataExpire(username+"level", "1", 300L);
            // 해당 정보를 client 에서 처리하게 좋을지 서버에서 처리하는게 좋을지
        // client 에 저장하면 이동 시 노출위험 -> 암호화 필수 but 서버 부담 감소
// stateless 구조 설계 권장
        return res;
    }

    // 회원가입 2. Email 인증
    @PostMapping("/api/mailcheck")
    public @ResponseBody ResponseEntity<String> mailCheck(@RequestBody String username) {
        // 헤더에 전달
//        int level = Integer.parseInt(String.valueOf(headers.get("level")));
//        if (level < 1) {
//            ResponseEntity<String> res = ResponseEntity.ok()
//                    .headers(headers)
//                    .body(emailService.joinEmail(username));

            //  redis 에 저장
        String level = redisUtil.getData(username+"level");
        ResponseEntity<String> res = ResponseEntity.ok(emailService.joinEmail(username));
        int val = Integer.parseInt(level);
        if (val < 1) {
            return ResponseEntity.ok("잘못된 접근입니다.");
        }
        redisUtil.setDataExpire(username+"level", "2", 300L);
            return res;
//        }
//        return null;
    }

    // 회원가입 3. Email 인증번호 확인
    @PostMapping("/api/checkAuthNum")
    public @ResponseBody ResponseEntity<String> checkAuthNum(@RequestBody String authNum, String username) {
        String level = redisUtil.getData(username+"level");
        ResponseEntity<String> res = ResponseEntity.ok(emailService.checkAuthNum(authNum));
        int val = Integer.parseInt(level);
        if (val < 2) {
            return ResponseEntity.ok("잘못된 접근입니다.");
        }
        redisUtil.setDataExpire(username+"level", "3", 300L);
        return res;
    }

    // 회원가입 4. password check
    @PostMapping("/api/checkpassword")
    public ResponseEntity<String> checkPassword(@RequestBody String password, @RequestBody String passwordCheck, String username) {
        String level = redisUtil.getData(username+"level");
        ResponseEntity<String> res = ResponseEntity.ok(memberService.checkPassword(password, passwordCheck));
        int val = Integer.parseInt(level);
        if (val < 3) {
            return ResponseEntity.ok("잘못된 접근입니다.");
        }
        redisUtil.setDataExpire(username+"level", "4", 300L);
        return res;
    }
    // 회원가입 5. 가입완료
    @PostMapping("/api/signup")
    public ResponseEntity<String > signup(@RequestBody MemberRequestDto requestDto) throws IOException {
        String level = redisUtil.getData(requestDto.getUsername()+"level");
        ResponseEntity<String> res = ResponseEntity.ok(memberService.signup(requestDto));
        int val = Integer.parseInt(level);
        if (val < 4) {
            return ResponseEntity.ok("잘못된 접근입니다.");
        }
        redisUtil.deleteData(requestDto.getUsername()+"level");
        return res;
    }

    // 회원 탈퇴
    @DeleteMapping("/api/withdraw")
    public ResponseEntity<String> withdraw(@AuthenticationPrincipal UserDetailsImpl userDetails, String password){
        return ResponseEntity.ok(memberService.withdraw(userDetails, password));
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
        Map<String, String> tokens = memberService.refresh(request, response);
        return ResponseEntity.ok(tokens);
    }

    // password 찾기
    @PostMapping("/api/findpassword")
    public ResponseEntity<String> findpassword(@RequestBody EmailDto requestDto) {
        memberService.findpassword(requestDto.getUsername());
        return ResponseEntity.ok("password 찾기 완료");
    }

    // 활동페이지 조회
    @GetMapping("/api/mypage/action")
    public ResponseEntity<MypageDto> action(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseEntity.ok(memberService.action(userDetails));
    }

    // 활동페이지 -> nickname 수정
    @PutMapping("/api/mypage/actionedit/nickname")
    public ResponseEntity<MypageDto> nicknameedit(@AuthenticationPrincipal UserDetailsImpl userDetails, String nickname) {
        return ResponseEntity.ok(memberService.nicknameedit(userDetails, nickname));
    }

    // 활동페이지 -> concern 수정
    @PutMapping("/api/mypage/actionedit/concern")
    public ResponseEntity<MypageDto> concernedit(@AuthenticationPrincipal UserDetailsImpl userDetails, List<Concern> concern) {
        return ResponseEntity.ok(memberService.concernedit(userDetails, concern));
    }

    //myInfo 페이지
    @GetMapping("/api/mypage/info")
    public ResponseEntity<MyPageInfoDto> myinfo(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseEntity.ok(memberService.myinfo(userDetails));
    }

    // myinfo -> gender 수정
    @PutMapping("/api/mypage/infoedit/gender")
    public ResponseEntity<MyPageInfoDto> genderedit(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                  InfoeditRequestDto requestDto) {
        return ResponseEntity.ok(memberService.genderedit(userDetails, requestDto));
    }

    // myinfo -> birth 수정
    @PutMapping("/api/mypage/infoedit/birth")
    public ResponseEntity<MyPageInfoDto> birthedit(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                  InfoeditRequestDto requestDto) {
        return ResponseEntity.ok(memberService.birthedit(userDetails, requestDto));
    }

    // profile 조회
    @GetMapping("/api/mypage/profile")
    public ResponseEntity<ProfileDto> profile(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseEntity.ok(memberService.profile(userDetails));
    }

    // profile 수정
    @PutMapping("/api/mypage/profileedit")
    public ResponseEntity<ProfileDto> profileedit(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                  ProfileEditRequestDto requestDto) {
        return ResponseEntity.ok(memberService.profileedit(userDetails, requestDto));
    }

    // login test
    @GetMapping("/api/loginForm")
    public String loginFrom() {
        return "login.html";
    }

    // password 변경
    @PostMapping("/api/changepass")
    public ResponseEntity<String> changepass(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                             @RequestBody String passwordcheck,
                                             String newpassword){
        return ResponseEntity.ok(memberService.changepass(userDetails, passwordcheck, newpassword));
    }

    // kakao login
    @GetMapping("/api/kakaologin")
    public ResponseEntity<String> kakaoLogin(@RequestParam String code) throws JsonProcessingException {
        // authorizedCode: 카카오 서버로부터 받은 인가 코드
        kakaoService.kakaoLogin(code);
        return  ResponseEntity.ok("redirect:/login");
    }

    // OAuth 로그인을 해도 UserDetailsImpl
    // 일반 로그인을 해도 UserDetailsImpl
    @GetMapping("/user")
    public @ResponseBody String loginTest(@AuthenticationPrincipal UserDetailsImpl userDetails){
        System.out.println("유저디테일"+userDetails);
        System.out.println("principalDetails: "+userDetails.getMember());
        return "user";
    }

}
