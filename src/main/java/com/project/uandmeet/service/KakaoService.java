package com.project.uandmeet.service;

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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Transactional
@RequiredArgsConstructor
@Service
public class KakaoService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisUtil redisUtil;


    public Map<String, String> kakaoLogin(String code) throws JsonProcessingException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String kakaoToken = getKakoToken(code);

        // 2. 토큰으로 카카오 API 호출
        KakaoUserInfoDto kakaoUserInfo = getKaKaoUserInfo(kakaoToken);

        // 필요 시 회원가입
        return registerKakaoIfNeeded(kakaoUserInfo);
    }


    private String getKakoToken(String code) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        // rest Api key
        body.add("client_id", "e789d33a46a6c7fd347b5f73e7669177");
//        body.add("redirect_uri", "http://localhost:8080/user/kakao/callback");
        body.add("redirect_uri", "http://localhost:3000/user/kakao/callback");
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
        String gender = jsonNode.get("kakao_account")
                        .get("gender").asText();
        System.out.println("jsonNode :"+jsonNode);
        System.out.println("카카오 사용자 정보: " + nickname + ", " + email + ", " + gender);
        return new KakaoUserInfoDto(nickname, email, gender);
    }


    private Map<String, String> registerKakaoIfNeeded(KakaoUserInfoDto kakaoUserInfo) {
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
            String gender = kakaoUserInfo.getGender();

            // password: random UUID
            String password = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(password);

            // role: 일반 사용자
            MemberRoleEnum role = MemberRoleEnum.USER;


            member = new Member(nickname, encodedPassword, username, gender);
            member.setLoginto("kakao");
            memberRepository.save(member);
            return createToken(member);
        } else {
            return createToken(member);
        }
    }

    private Map<String, String> createToken(Member member) {

        String accessToken = jwtTokenProvider.createToken(member.getUsername(), member.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getUsername());

        // redis 에 token 저장
        redisUtil.setDataExpire(member.getUsername()+JwtProperties.HEADER_ACCESS, JwtProperties.TOKEN_PREFIX + accessToken, JwtProperties.ACCESS_EXPIRATION_TIME);
        redisUtil.setDataExpire(member.getUsername()+JwtProperties.HEADER_REFRESH, JwtProperties.TOKEN_PREFIX + refreshToken, JwtProperties.REFRESH_EXPIRATION_TIME);


        // token 을 Header 에 발급
        // 재발급떼문에 set 사용
        HttpHeaders headers = new HttpHeaders();
        Map<String, String> userInfo = new HashMap<>();
        headers.set(JwtProperties.HEADER_ACCESS, JwtProperties.TOKEN_PREFIX + accessToken);
        userInfo.put("username", member.getUsername());
        userInfo.put("nickname", member.getNickname());
        userInfo.put("profile", member.getProfile());
        userInfo.put("loginto", member.getLoginto());
        // 헤더에 보이지 않음
        headers.set("username", member.getUsername());
        headers.set("nickname", member.getNickname());
        headers.set("profile", member.getProfile());
        headers.set("loginto", member.getLoginto());

        return userInfo;
    }
}
