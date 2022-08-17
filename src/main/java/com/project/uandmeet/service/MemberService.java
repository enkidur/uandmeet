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
    private final EmailService emailService;

    // 회원 가입
    public String join(MemberRequestDto requestDto) throws IOException {
        String email = requestDto.getUsername();
        String[] emailadress = email.split("@");
        String id = emailadress[0];
        String host = emailadress[1];
        String password = requestDto.getPassword();
        String passwordCheck = requestDto.getPasswordCheck();
//        String pattern = "^[a-zA-Z0-9]*$";
        String pattern = "^[a-zA-Z0-9_!#$%&'\\*+/=?{|}~^.-]+@[a-zA-Z0-9.-]+.[a-zA-Z0-9.-]*$";
        String idpattern = "^[a-zA-Z0-9_!#$%&'\\*+/=?{|}~^.-]*$";
        String hostpattern ="^[a-zA-Z0-9.-]*$";
        // email 조건
        // ID 영문 대소문자, 숫자, _!#$%&'\*+/=?{|}~^.- 특문허용
        // Host 시작전 @, 영문 대소문자, 숫자, .-특문허용

        // 회원가입 조건
        if (email.length() < 10) {
            throw new IllegalArgumentException("이메일을 10자 이상 입력하세요");
        } else if (!Pattern.matches(idpattern, id)) {
            throw new IllegalArgumentException("id에 알파벳 대소문자와 숫자, 특수기호( _!#$%&'\\*+/=?{|}~^.-)로만 입력하세요");
        } else if (!Pattern.matches(hostpattern, host)) {
            throw new IllegalArgumentException("host에 알파벳 대소문자와 숫자, 특수기호(.-)로만 입력하세요");
        } else if (!Pattern.matches(pattern, email)) {
            throw new IllegalArgumentException("이메일 규격에 맞게 입력하세요");
        } else if (password.length() < 3) {
            throw new IllegalArgumentException("비밀번호를 3자 이상 입력하세요");
        } else if (password.length() > 13) {
            throw new IllegalArgumentException("비밀번호를 12자 이하 입력하세요");
        } else if (password.contains(id)) {
            throw new IllegalArgumentException(" 비밀번호에 id를 포함할 수없습니다.");
        }else if (email.contains("script") || email.contains("<") || email.contains(">")) {
            throw new IllegalArgumentException("xss공격 멈춰주세요.");
        }else if (password.contains("script") || password.contains("<") || password.contains(">")) {
            throw new IllegalArgumentException("xss공격 멈춰주세요.");
        }else if (passwordCheck.contains("script") || passwordCheck.contains("<") || passwordCheck.contains(">")) {
            throw new IllegalArgumentException("xss공격 멈춰주세요.");
        }

        // email 중복 확인
        checkDuplicateEmail(email);

        // email 인증 번호 발송
        emailService.joinEmail(email);
        // 인증 번호 확인 절차는 controller 에서 실행

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

    public void checkDuplicateEmail(String username) {
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
//        String username = decodedJWT.getSubject();
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
//                .withClaim("email", member.getUsername())
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
        if (diffDays < 2) { // refresh token 만료시간이 특정시간보다 작으면 재발급
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
        response.setHeader(JwtProperties.HEADER_ACCESS, tokens.get(JwtProperties.HEADER_ACCESS));
        if (tokens.get(JwtProperties.HEADER_REFRESH) != null) {
            response.setHeader(JwtProperties.HEADER_REFRESH, tokens.get(JwtProperties.HEADER_REFRESH));
        }
        return tokens;
    }

}
