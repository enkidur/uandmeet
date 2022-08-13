package com.project.uandmeet.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.project.uandmeet.dto.MemberRequestDto;
import com.project.uandmeet.jwt.JwtProperties;
import com.project.uandmeet.model.Member;
import com.project.uandmeet.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Transactional
@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // 회원 가입
    public String join(MemberRequestDto requestDto) throws IOException {
        String username = requestDto.getUsername();
        String password = requestDto.getPassword();
        String passwordCheck = requestDto.getPasswordCheck();
        String pattern = "^[a-zA-Z0-9]*$";

        // 회원가입 조건
        if (username.length() < 3) {
            throw new IllegalArgumentException("닉네임을 3자 이상 입력하세요");
        } else if (!Pattern.matches(pattern, username)) {
            throw new IllegalArgumentException("알파벳 대소문자와 숫자로만 입력하세요");
        } else if (password.length() < 3) {
            throw new IllegalArgumentException("비밀번호를 3자 이상 입력하세요");
        } else if (password.contains(username)) {
            throw new IllegalArgumentException("비밀번호에 닉네임을 포함할 수 없습니다.");
        }

        // username 중복 확인
        checkDuplicateUsername(username);

        // 비밀번호, 비밀번호 재입력 확인
        checkPassword(password, passwordCheck);

        Member member = requestDto.register();
        member.setPassword(passwordEncoder.encode(requestDto.getPassword()));

        // 프로필 이미지 추가
//        if (requestDto.getUserProfileImage() != null) {
//            String profileUrl = s3Uploader.upload(requestDto.getUserProfileImage(), "profile");
//            users.setUserProfileImage(profileUrl);
//        }
//
        memberRepository.save(member);
        return "회원가입 완료";
    }

    public void checkDuplicateUsername(String username) {
        Optional<Member> users = memberRepository.findByUsername(username);
        if (users.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 계정입니다.");
        }
    }

    private void checkPassword(String password, String passwordCheck) {
        if (!(passwordCheck.equals(password))) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    // TOKEN

    public void updateRefreshToken(String username, String refreshToken) {
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        member.updateRefreshToken(refreshToken);
    }


    public Map<String, String> refresh(HttpServletRequest request, HttpServletResponse response) {

        // refreshToken
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith(JwtProperties.TOKEN_PREFIX)) {
            throw new RuntimeException("JWT Token이 존재하지 않습니다.");
        }
        String refreshToken = authorizationHeader.substring(JwtProperties.TOKEN_PREFIX.length());

        // Refresh Token 유효성 검사
        // 토큰 해독 객체 생성
        JWTVerifier verifier = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET2)).build();
        // 토큰 검증
        DecodedJWT decodedJWT = verifier.verify(refreshToken);

        // 그냥 합쳐서 사용가능, 가독성을 위해 분해
//        String username =
//                JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build()
//                        .verify(refreshToken).getClaim("username").asString();
//
//        or
//
//        String username =
//                JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build()
//                        .verify(refreshToken).getSubject();



        // Access Token 재발급
        long now = System.currentTimeMillis();
        String username = decodedJWT.getSubject();
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        if (!member.getRefreshToken().equals(refreshToken)) {
            throw new JWTVerificationException("유효하지 않은 Refresh Token 입니다.");
        }
        String accessToken = JWT.create()
                .withSubject(member.getUsername()) // PrincipalDetails 에서 가져오는 방법 찾는 중
                .withExpiresAt(new Date(now + JwtProperties.ACCESS_EXPIRATION_TIME))
                .withClaim("id", member.getId())
                .withClaim("username", member.getUsername())
                // map 은 스트림 내 요소들을 하나씩 특정 값으로 변환
                // Role 을 name 으로 매핑 (Role 의 name 을 꺼내옴)
//                .withClaim("roles", member.getRoles().stream().map(Role::getName)
//                        .collect(Collectors.toList()))
                .withIssuedAt(new Date(now))
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));


        Map<String, String> accessTokenResponseMap = new HashMap<>();

        // 현재시간과 Refresh Token 만료날짜를 통해 남은 만료기간 계산 (만료기간 전 재발급이 필요없다면 삭제)
        // Refresh Token 만료시간 계산해 특정 시간 미만일 시 refresh token 도 재발급
        long refreshExpireTime = decodedJWT.getClaim("exp").asLong() * 1000;
        long diffDays = (refreshExpireTime - now) / 1000 / (24 * 3600);
        long diffMin = (refreshExpireTime - now) / 1000 / 60;
        if (diffMin < 5) { // refresh token 만료시간이 5분 보다 작으면 재발급
            String newRefreshToken = JWT.create()
                    .withSubject(member.getUsername())
                    .withExpiresAt(new Date(now + JwtProperties.REFRESH_EXPIRATION_TIME))
                    .withIssuedAt(new Date(now))
                    .sign(Algorithm.HMAC512(JwtProperties.SECRET2));
            accessTokenResponseMap.put(JwtProperties.HEADER_REFRESH, JwtProperties.TOKEN_PREFIX+newRefreshToken);
            // db 에 new refreshToken 저장
            member.updateRefreshToken(newRefreshToken);
        }

        accessTokenResponseMap.put(JwtProperties.HEADER_ACCESS, JwtProperties.TOKEN_PREFIX+accessToken);
        Map<String, String> tokens = accessTokenResponseMap;
        String test = tokens.get(JwtProperties.HEADER_REFRESH);
        response.setHeader(JwtProperties.HEADER_ACCESS, tokens.get(JwtProperties.HEADER_ACCESS));
        if (tokens.get(JwtProperties.HEADER_REFRESH) != null) {
            response.setHeader(JwtProperties.HEADER_REFRESH, tokens.get(JwtProperties.HEADER_REFRESH));
        }
        return tokens;
    }

}
