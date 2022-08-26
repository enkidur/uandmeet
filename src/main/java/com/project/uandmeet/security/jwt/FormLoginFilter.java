//package com.project.uandmeet.security.jwt;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.project.uandmeet.dto.LoginRequestDto;
//import com.project.uandmeet.redis.RedisUtil;
//import com.project.uandmeet.security.UserDetailsImpl;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//public class FormLoginFilter extends UsernamePasswordAuthenticationFilter {
//    @Autowired
//    private final JwtTokenProvider jwtTokenProvider;
//    @Autowired
//    private final RedisUtil redisUtil;
//
//
//    public FormLoginFilter(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider,RedisUtil redisUtil) {
//        super(authenticationManager);
//        this.jwtTokenProvider = jwtTokenProvider;
//        this.redisUtil = redisUtil;
//    }
//
//
//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
//        try {
//            ObjectMapper om = new ObjectMapper();
//
//            LoginRequestDto loginRequestDto = om.readValue(request.getInputStream(), LoginRequestDto.class);
//
//            UsernamePasswordAuthenticationToken authenticationToken =
//                    new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword());
//
//            System.out.println("FormLoginFilter를 거침");
//
//            return getAuthenticationManager().authenticate(authenticationToken);
//
//        } catch (IOException e) {
//            throw new RuntimeException("잘못된 로그인 정보입니다.");
////            e.printStackTrace();
//        }
//    }
//
//    @Override
//    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
//        UserDetailsImpl userDetails = (UserDetailsImpl) authResult.getPrincipal();
//
//        jwtTokenProvider.createToken(userDetails.getUsername());
//        String refreshToken = jwtTokenProvider.createRefreshToken();
//
//        redisUtil.setDataExpire(userDetails.getUsername(),refreshToken,JwtProperties.REFRESH_EXPIRATION_TIME);
//
//    }
//}