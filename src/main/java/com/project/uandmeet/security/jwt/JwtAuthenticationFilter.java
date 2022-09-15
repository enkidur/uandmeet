package com.project.uandmeet.security.jwt;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.project.uandmeet.dto.LoginRequestDto;
import com.project.uandmeet.model.Member;
import com.project.uandmeet.redis.RedisUtil;
import com.project.uandmeet.security.UserDetailsImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor // final 생성자 자동 생성
//@AllArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final RedisUtil redisUtil;
    private final JwtTokenProvider jwtTokenProvider;

    // Authentication 객체 만들어서 리턴 => 의존 : AuthenticationManager
    // 인증 요청시에 실행되는 함수 => /login
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        System.out.println("JwtAuthenticationFilter : 진입");
        // 1. request에 있는 username과 password를 파싱해서 자바 Object로 받기
        ObjectMapper om = new ObjectMapper();
        LoginRequestDto loginRequestDto = null;
        try {
            // x-www.form-urlencoded 방식
//            BufferedReader br = request.getReader();
//
//            String input = null;
//            while ((input = br.readLine()) != null) {
//                System.out.println(input);
//            }
            // json 방식
            loginRequestDto = om.readValue(request.getInputStream(), LoginRequestDto.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("JwtAuthenticationFilter : " + loginRequestDto);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getUsername(),
                        loginRequestDto.getPassword());

        System.out.println("JwtAuthenticationFilter : 토큰생성완료");
        Authentication authentication =
                authenticationManager.authenticate(authenticationToken);

        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
        System.out.println("Authentication : " + userDetailsImpl.getMember().getUsername());
        return authentication;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws UnsupportedEncodingException {

        // 해당 principalDetails 정보를 통해 Jwt token 생성
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authResult.getPrincipal();

        // Hash 방식
        String accessToken = jwtTokenProvider.createToken(userDetailsImpl.getUsername(), userDetailsImpl.getMember().getId());

        String refreshToken = jwtTokenProvider.createRefreshToken(userDetailsImpl.getUsername());


        redisUtil.setDataExpire(userDetailsImpl.getUsername()+JwtProperties.HEADER_ACCESS, JwtProperties.TOKEN_PREFIX + accessToken, JwtProperties.ACCESS_EXPIRATION_TIME);
        redisUtil.setDataExpire(userDetailsImpl.getUsername()+JwtProperties.HEADER_REFRESH, JwtProperties.TOKEN_PREFIX + refreshToken, JwtProperties.REFRESH_EXPIRATION_TIME);
        String nickname = URLEncoder.encode(userDetailsImpl.getMember().getNickname(),"utf-8");
        response.setHeader(JwtProperties.HEADER_ACCESS, JwtProperties.TOKEN_PREFIX + accessToken);
        response.setHeader("username", userDetailsImpl.getUsername());
        response.setHeader("nickname", nickname);
        response.setHeader("profile", userDetailsImpl.getMember().getProfile());
        response.setHeader("loginto", userDetailsImpl.getMember().getLoginto());
    }

}