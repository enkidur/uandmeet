package com.project.uandmeet.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.uandmeet.dto.MemberRequestDto;
import com.project.uandmeet.service.KakaoService;
import com.project.uandmeet.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

//@RestController
@Controller
@RequestMapping
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final KakaoService kakaoService;

    @GetMapping("api/join")
    public String joinpage() {
        return "join";
    }

    @PostMapping("api/join")
    public String join(@RequestBody MemberRequestDto requestDto) throws IOException {
        memberService.join(requestDto);
        return "redirect:/login";
    }

    // kakao

    @GetMapping("/user/kakao/callback")
    public String kakaoLogin(@RequestParam String code) throws JsonProcessingException {
        // authorizedCode: 카카오 서버로부터 받은 인가 코드
        kakaoService.kakaoLogin(code);
        return "redirect:/login";
    }

}
