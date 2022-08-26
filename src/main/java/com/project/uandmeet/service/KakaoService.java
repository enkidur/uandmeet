package com.project.uandmeet.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.uandmeet.dto.KakaoUserInfoDto;
import com.project.uandmeet.dto.SocialLoginInfoDto;
import com.project.uandmeet.model.Member;
import com.project.uandmeet.model.MemberRoleEnum;
import com.project.uandmeet.redis.RedisUtil;
import com.project.uandmeet.repository.MemberRepository;
import com.project.uandmeet.security.UserDetailsImpl;
import com.project.uandmeet.security.jwt.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class KakaoService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    public KakaoUserInfoDto kakaoLogin(String code, HttpServletResponse response) throws JsonProcessingException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String kakaoToken = getKakoToken(code);

        // 2. 토큰으로 카카오 API 호출
        KakaoUserInfoDto kakaoUserInfo = getKaKaoUserInfo(kakaoToken);

        // 필요 시 회원가입
        Member foundMember = registerKakaoIfNeeded(kakaoUserInfo);

        // 4. 강제 로그인 처리
        Authentication authentication = securityLogin(foundMember);

        // 4. accessToken, refreshToken 발급
        createToken(response, authentication);
        return kakaoUserInfo;
    }


    //인가코드로 액세스 토큰 가져오기
    private String getKakoToken(String code) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        // rest Api key
        body.add("client_id", "5d309e8e3962145e21700ba232a4d3bc");
        body.add("redirect_uri", "http://localhost:8080/user/kakao/callback");
        body.add("code", code);
        body.add("grant_type", "authorization_code");

        // HTTP POST 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate(); // RestTemplate 서버 대 서버 요청
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }

    //액세스 토큰으로 유저 정보 가져오기

    private KakaoUserInfoDto getKaKaoUserInfo(String kakaoToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + kakaoToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate(); // RestTemplate 서버 대 서버 요청
        ResponseEntity<String> response = rt.exchange(
                // 해당 주소로 token 보내면 사용자 정보 호출
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
//        String id = jsonNode.get("username").asText();
        String nickname = jsonNode.get("properties")
                .get("nickname").asText();
        String email = jsonNode.get("kakao_account")
                .get("email").asText();

        System.out.println("카카오 사용자 정보: " + nickname + ", " + email);
        KakaoUserInfoDto kakaoUserInfoDto = new KakaoUserInfoDto(nickname, email);
        return kakaoUserInfoDto;
    }

//  유저 확인 및 회원가입
    private Member registerKakaoIfNeeded(KakaoUserInfoDto kakaoUserInfo) {
        // DB 에 중복된 Kakao Id 가 있는지 확인
//        String id = kakaoUserInfo.getId();
//        System.out.println(id);
        // username: kakao email
        String username = kakaoUserInfo.getEmail();
        Member member = memberRepository.findByUsername(username)
                .orElse(null);
        if (member == null) {
            // 회원가입
            String nickname = kakaoUserInfo.getNickname();

            // password: random UUID
            String password = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(password);

            // role: 일반 사용자
            MemberRoleEnum role = MemberRoleEnum.USER;


            member = new Member(nickname, encodedPassword, username);
            member.setLoginto("kakao");
            memberRepository.save(member);
        } else {
            throw new RuntimeException("이미 존재하는 계정입니다.");
        }
        return member;
    }

    //시큐리티 강제 로그인

    private Authentication securityLogin(Member findMember) {

        // userDetails 생성
        UserDetailsImpl userDetails = new UserDetailsImpl(findMember);
        log.warn("kakao 로그인 완료 : " + userDetails.getMember().getUsername());
        // UsernamePasswordAuthenticationToken 발급
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        // 강제로 시큐리티 세션에 접근하여 authentication 객체를 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    //토큰 발급
    private void createToken(HttpServletResponse response, Authentication authentication) {

        UserDetailsImpl userDetailsImpl = ((UserDetailsImpl) authentication.getPrincipal());
        String token = JwtTokenUtils.generateJwtToken(userDetailsImpl);
        response.addHeader("Authorization", "BEARER" + " " + token);

    }

//    private void froceLogin(Member member) {
//        UserDetailsImpl userDetails = new UserDetailsImpl(member);
//        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,userDetails.getAuthorities());
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//    }
}
