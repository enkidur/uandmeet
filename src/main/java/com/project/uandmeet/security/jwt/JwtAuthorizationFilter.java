package com.project.uandmeet.security.jwt;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.project.uandmeet.redis.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;


// 인가
// Security Filter 의 BasicAuthenticationFilter 는 상시 실행되나
// 권한이나 인증이 필요한 주소를 요청했을 때 token 의 유무를 검사
@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private RedisUtil redisUtil;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        super(authenticationManager);
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // 인증이나 권한이 필요한 요청이 있을 때 해당 filter 를 실행
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String header = request.getHeader(JwtProperties.HEADER_ACCESS);
        // 로그인, 리프레시 요청이라면 토큰 검사하지 않음
        if (request.getServletPath().equals("/login") || request.getServletPath().equals("/api/refresh") || request.getServletPath().equals("/user/kakao/callback")) {
            chain.doFilter(request, response);
        }

        if (header == null || !header.startsWith(JwtProperties.TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }
        System.out.println("header : " + header);
        String token = jwtTokenProvider.setTokenName(header);

        // 최신 토큰인지 검증
//        if (!redisUtil.getData(jwtTokenProvider.getUserPk(token)+JwtProperties.HEADER_ACCESS).equals(header)) {
//            throw new RuntimeException("잘못된 AccessToken입니다.");
//        }

        // 유효한 토큰인지 확인
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 토큰이 유효하면 토큰으로부터 유저 정보를 받아와서 저장
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
//        else if(token != null && !jwtTokenProvider.validateToken(token)){
//            String result = jwtTokenProvider.resolveRefreshToken(request);
//
//            if(result == null){
//                throw new JwtException("access token 이 만료 되었습니다.");
//            }
//        }
        chain.doFilter(request, response);
    }
}