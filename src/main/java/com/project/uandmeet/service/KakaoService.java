package com.project.uandmeet.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.uandmeet.dto.KakaoUserInfoDto;
import com.project.uandmeet.model.Member;
import com.project.uandmeet.model.MemberRoleEnum;
import com.project.uandmeet.redis.RedisUtil;
import com.project.uandmeet.repository.MemberRepository;
import com.project.uandmeet.security.jwt.JwtProperties;
import com.project.uandmeet.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.UUID;

@Transactional
@RequiredArgsConstructor
@Service
public class KakaoService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisUtil redisUtil;


    public void kakaoLogin(String code) throws JsonProcessingException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String kakaoToken = getKakoToken(code);

        // 2. 토큰으로 카카오 API 호출
        KakaoUserInfoDto kakaoUserInfo = getKaKaoUserInfo(kakaoToken);

        // 필요 시 회원가입
        Member member = registerKakaoIfNeeded(kakaoUserInfo);

        // 4. 강제 로그인 처리
//        froceLogin(member);

        // 4. accessToken, refreshToken 발급
        createToken(member);

    }


    private String getKakoToken(String code) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        // rest Api key
        body.add("client_id", "5d309e8e3962145e21700ba232a4d3bc");
        body.add("redirect_uri", "http://localhost:8080/user/kakao/callback");
        body.add("code", code);

        // HTTP 요청 보내기
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

    private void createToken(Member member) {

        String accessToken = jwtTokenProvider.createToken(member.getUsername(), member.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken();

//        String accessToken = JWT.create()
//                .withSubject(member.getUsername())
//                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.ACCESS_EXPIRATION_TIME))
//                .withClaim("id", member.getId())
////                .withClaim("email", member.getUsername())
//                .withIssuedAt(new Date(System.currentTimeMillis()))
//                .sign(Algorithm.HMAC512(JwtProperties.SECRET));
//
//        String refreshToken = JWT.create()
//                .withSubject(member.getUsername())
//                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.REFRESH_EXPIRATION_TIME))
//                .withIssuedAt(new Date(System.currentTimeMillis()))
//                .sign(Algorithm.HMAC512(JwtProperties.SECRET2));

        // Refresh Token DB에 저장
//        memberService.updateRefreshToken(member.getUsername(), refreshToken);
        // redis 에 token 저장
        redisUtil.setDataExpire(member.getUsername(),refreshToken,JwtProperties.REFRESH_EXPIRATION_TIME);


        // token 을 Header 에 발급
        // 재발급떼문에 set 사용
        HttpHeaders headers = new HttpHeaders();
        headers.set(JwtProperties.HEADER_ACCESS, JwtProperties.TOKEN_PREFIX + accessToken);
        headers.set(JwtProperties.HEADER_REFRESH, JwtProperties.TOKEN_PREFIX + refreshToken);
    }

//    private void froceLogin(Member member) {
//        UserDetailsImpl userDetails = new UserDetailsImpl(member);
//        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,userDetails.getAuthorities());
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//    }
}
