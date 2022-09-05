package com.project.uandmeet.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.uandmeet.dto.*;
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


    // 회원가입 1. emali check
    @PostMapping("/api/checkemail")
    public ResponseEntity<String> checkemail(@RequestBody EmailDto requestDto, @RequestBody EmailDto emailDto) throws IOException {
//        // 헤더에 전달
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("level","1");
//        ResponseEntity<String> res = ResponseEntity.ok()
//                                     .headers(headers)
//                                     .body(memberService.checkemail(username));
//        log.info(String.valueOf(res.getHeaders()));
//         redis 에 저장
        memberService.checkemail(requestDto.getUsername());
        ResponseEntity<String> res = ResponseEntity.ok(emailService.joinEmail(emailDto.getUsername()));
//        redisUtil.setDataExpire(username + "level", "1", 300L);
        // 해당 정보를 client 에서 처리하게 좋을지 서버에서 처리하는게 좋을지
        // client 에 저장하면 이동 시 노출위험 -> 암호화 필수 but 서버 부담 감소
// stateless 구조 설계 권장
        return res;
    }

    // 회원가입 2. Email 인증
//    @PostMapping("/api/mailcheck")
//    public @ResponseBody ResponseEntity<String> mailCheck(@RequestBody EmailDto emailDto) throws IOException {
//        // 헤더에 전달
////        int level = Integer.parseInt(String.valueOf(headers.get("level")));
////        if (level < 1) {
////            ResponseEntity<String> res = ResponseEntity.ok()
////                    .headers(headers)
////                    .body(emailService.joinEmail(username));
//
//        //  redis 에 저장
//        String level = redisUtil.getData(emailDto.getUsername() + "level");
//        ResponseEntity<String> res = ResponseEntity.ok(emailService.joinEmail(emailDto.getUsername()));
//        redisUtil.setDataExpire(emailDto.getUsername() + "level", "2", 300L);
//        return res;
////        }
////        return null;
//    }

    // 회원가입 3. Email 인증번호 확인
    @PostMapping("/api/checkAuthNum")
    public @ResponseBody ResponseEntity<String> checkAuthNum(@RequestBody AuthNumDto requestDto, @RequestBody MemberRequestDto memberRequestDto) throws IOException {
//        String level = redisUtil.getData(username + "level");
        emailService.checkAuthNum(requestDto.getAuthNum());
        memberService.checkPassword(memberRequestDto.getPassword(), memberRequestDto.getPasswordCheck());
        ResponseEntity<String> res = ResponseEntity.ok(memberService.signup(memberRequestDto));
//        int val = Integer.parseInt(level);
//        if (val < 2) {
//            return ResponseEntity.ok("잘못된 접근입니다.");
//        }
//        redisUtil.setDataExpire(username + "level", "3", 300L);
        return res;
    }

    // 회원가입 4. password check
//    @PostMapping("/api/checkpassword")
//    public ResponseEntity<String> checkPassword(@RequestBody PasswordDto passwordDto) {
//        String level = redisUtil.getData(username + "level");
//        ResponseEntity<String> res = ResponseEntity.ok(memberService.checkPassword(passwordDto.getPassword(), passwordDto.getPasswordCheck()));
//        int val = Integer.parseInt(level);
//        if (val < 3) {
//            return ResponseEntity.ok("잘못된 접근입니다.");
//        }
//        redisUtil.setDataExpire(username + "level", "4", 300L);
//        return res;
//    }

    // 회원가입 5. 가입완료
//    @PostMapping("/api/signup")
//    public ResponseEntity<String> signup(@RequestBody MemberRequestDto requestDto) throws IOException {
//        String level = redisUtil.getData(requestDto.getUsername() + "level");
//        ResponseEntity<String> res = ResponseEntity.ok(memberService.signup(requestDto));
//        int val = Integer.parseInt(level);
//        if (val < 4) {
//            return ResponseEntity.ok("잘못된 접근입니다.");
//        }
//        redisUtil.deleteData(requestDto.getUsername() + "level");
//        return res;
//    }

    // 회원가입 test
    @PostMapping("/api/join")
    public void join(@RequestBody MemberRequestDto requestDto) {
        memberService.join(requestDto);

    }

    // 회원 탈퇴
    @DeleteMapping("/api/withdraw")
    public ResponseEntity<String> withdraw(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                           @RequestBody PasswordDto requestDto) {
        return ResponseEntity.ok(memberService.withdraw(userDetails, requestDto.getPassword()));
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
    public ResponseEntity<Map<String, String>> refresh(HttpServletRequest request,
                                                       HttpServletResponse response,
                                                       @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Map<String, String> tokens = memberService.refresh(request, response, userDetails);
        return ResponseEntity.ok(tokens);
    }

    //  password 찾기 1. password 찾기 -> 인증번호 발송
    @PostMapping("/api/findPassword")
    public ResponseEntity<String> findpassword(@RequestBody EmailDto requestDto) {
        memberService.findpassword(requestDto.getUsername());
        return ResponseEntity.ok("인증번호");
    }
    // 인증 번호로 인증 받고 차후 비밀번호 변경

    // password 찾기 2. password 인증번호 확인
    @PostMapping("/api/findCheck")
    public ResponseEntity<String> findCheck(@RequestBody AuthNumDto requestDto) {
        memberService.findCheck(requestDto.getAuthNum());
        return ResponseEntity.ok("인증 완료");
    }

    //  password 찾기 3. password 변경
    @PostMapping("/api/passChange")
    public ResponseEntity<String> passChange(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                             @RequestBody PasswordChangeDto passwordChangeDto) {
        memberService.passChange(userDetails, passwordChangeDto);
        return ResponseEntity.ok("변경 완료");
    }

    // 활동페이지 조회
    @GetMapping("/api/mypage/action")
    public ResponseEntity<MypageDto> action(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(memberService.action(userDetails));
    }

    // 활동페이지 -> nickname 수정
    @PutMapping("/api/mypage/actionedit/nickname")
    public ResponseEntity<MypageDto> nicknameedit(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                  @RequestBody NicknameDto requestDto) {
        log.info(userDetails.getUsername());
        return ResponseEntity.ok(memberService.nicknameedit(userDetails, requestDto.getNickname()));
    }

    // 활동페이지 -> concern 수정
    @PutMapping("/api/mypage/actionedit/concern")
    public ResponseEntity<MypageDto> concernedit(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                 @RequestBody ConcernDto requestDto) {
        return ResponseEntity.ok(memberService.concernedit(userDetails,
                requestDto.getConcern1En(),
                requestDto.getConcern1Kor(),
                requestDto.getConcern2En(),
                requestDto.getConcern2Kor(),
                requestDto.getConcern3En(),
                requestDto.getConcern3Kor() ));
    }

    //myInfo 페이지
    @GetMapping("/api/mypage/info")
    public ResponseEntity<MyPageInfoDto> myinfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info(userDetails.getUsername());
        return ResponseEntity.ok(memberService.myinfo(userDetails));
    }

    // myinfo -> gender 수정
    @PutMapping("/api/mypage/infoedit/gender")
    public ResponseEntity<MyPageInfoDto> genderedit(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                    @RequestBody InfoeditRequestDto requestDto) {
        return ResponseEntity.ok(memberService.genderedit(userDetails, requestDto));
    }

    // myinfo -> birth 수정
    @PutMapping("/api/mypage/infoedit/birth")
    public ResponseEntity<MyPageInfoDto> birthedit(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                   @RequestBody InfoeditRequestDto requestDto) {
        return ResponseEntity.ok(memberService.birthedit(userDetails, requestDto));
    }

    // profile 조회
    @GetMapping("/api/mypage/profile")
    public ResponseEntity<ProfileDto> profile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(memberService.profile(userDetails));
    }

    // profile 수정
    @PutMapping("/api/mypage/profile")
    public ResponseEntity<ProfileDto> profileedit(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                  @RequestBody ProfileEditRequestDto requestDto) throws IOException {
        return ResponseEntity.ok(memberService.profileedit(userDetails, requestDto));
    }

    // 매칭 간단평가
    @GetMapping("/api/userinfo/simplereview")
    public ResponseEntity<Map<Integer, Long>> simpleReview(Long memberId) {
        return ResponseEntity.ok(memberService.simpleReview(memberId));
    }

    // 매칭 후기
    @GetMapping("/api/userinfo/review")
    public ResponseEntity<List<Review>> Review(Long memberId) {
        return ResponseEntity.ok(memberService.Review(memberId));
    }

    // login test
    @GetMapping("/api/loginForm")
    public String loginFrom() {
        return "login.html";
    }

    // password 변경
    @PostMapping("/api/changepass")
    public ResponseEntity<String> changepass(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                             @RequestBody PasswordChangeDto passwordChangeDto) {
        return ResponseEntity.ok(memberService.changepass(userDetails, passwordChangeDto));
    }

    // kakao login
    @GetMapping("/api/kakaoLogin")
    public ResponseEntity<String> kakaoLogin(@RequestParam String code) throws JsonProcessingException {
        // authorizedCode: 카카오 서버로부터 받은 인가 코드
        kakaoService.kakaoLogin(code);
        return ResponseEntity.ok("redirect:/login");
    }

    // OAuth 로그인을 해도 UserDetailsImpl
    // 일반 로그인을 해도 UserDetailsImpl
    @GetMapping("/user")
    public @ResponseBody String loginTest(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        System.out.println("유저디테일" + userDetails);
        System.out.println("principalDetails: " + userDetails.getMember());
        return "user";
    }

//    @GetMapping("/login/google")
//    public String tem(){
//        return "redirect:login.html";
//    }

}
