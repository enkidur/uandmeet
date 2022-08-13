package com.project.uandmeet.jwt;

import java.io.IOException;
import java.util.Date;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.project.uandmeet.auth.UserDetailsImpl;
import com.project.uandmeet.dto.LoginRequestDto;
import com.project.uandmeet.service.MemberService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

// Security 에서 UsernamePasswordAuthenticationFilter가 있음
// 해당 필터는 /login 요청해서 username, password 를 전송하면 (post) 작동
// but .fromLoin().disable() 때문에 UsernamePasswordAuthenticationFilter 작동을 안함
// 따라서 해당 filter 를 작동시키기 위해 securityConfig 에 등록
// UsernamePasswordAuthenticationFilter 는 AuthenticationManager 를 통해 login 진행 하기 때문에 AuthenticationManager 파라미터 필요

@RequiredArgsConstructor // final 생성자 자동 생성
//@AllArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter{

    private final AuthenticationManager authenticationManager;
    private final MemberService memberService;

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

        System.out.println("JwtAuthenticationFilter : "+loginRequestDto);

        // 유저네임패스워드 토큰 생성
        // token 생성 -> formLogin에서 자동 실행되나 disable 때문에 직접 생성
        // 해당 token 은 db 내용과 일치하는지 확인을 위한 인증 token
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getUsername(),
                        loginRequestDto.getPassword());

        System.out.println("JwtAuthenticationFilter : 토큰생성완료");

        // authenticate() 함수가 호출 되면 인증 프로바이더가 유저 디테일 서비스의
        // loadUserByUsername(토큰의 첫번째 파라메터) 를 호출하고
        // UserDetails를 리턴받아서 토큰의 두번째 파라메터(credential)과
        // UserDetails(DB값)의 getPassword()함수로 비교해서 동일하면
        // Authentication 객체를 만들어서 필터체인으로 리턴해준다.

        // Tip: 인증 프로바이더의 디폴트 서비스는 UserDetailsService 타입
        // Tip: 인증 프로바이더의 디폴트 암호화 방식은 BCryptPasswordEncoder
        // 결론은 인증 프로바이더에게 알려줄 필요가 없음.


        // 2. 정상인지 AuthenticationManager 로 login 시도하여 PrincipalDetailsService 호출되어 loadUserByUsername 메서드 실행
        // 값이 정상이라면 authentication 리턴 -> db 의 username, password 와 일치
        // Authentication 에는 user 의 login 정보가 담김
        // 정상적으로 생성된 Authentication
        Authentication authentication =
                authenticationManager.authenticate(authenticationToken);

        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
        System.out.println("Authentication : "+userDetailsImpl.getMember().getUsername());

        // 3. PrincipalDetails 에 session 을 담음 (권한 관리를 위해 Session에 담음)
        // 굳이 Jwt 를 사용하면서 session 을 만들 필요 없지만, 권한 처리를 위해
        // 세션아이디를 응답하지 않는 STATELESS 정책을 사용해도 괜찮은 이유는 세션영역은 임시 저장의 용도로만 사용
        // authentication 객체가 session 영역에 저장 (리턴을 통해 저장)
        return authentication;
    }

    // attemptAuthentication 에서 인증이 성공했다면 실행
    // JWT Token 생성해서 response에 담아주기
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult)
            throws IOException, ServletException {

        // 해당 principalDetails 정보를 통해 Jwt token 생성
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authResult.getPrincipal();

        // Hash 방식
        String accessToken = JWT.create()
                .withSubject(userDetailsImpl.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis()+JwtProperties.ACCESS_EXPIRATION_TIME))
                .withClaim("id", userDetailsImpl.getMember().getId())
                .withClaim("username", userDetailsImpl.getMember().getUsername())
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));

        String refreshToken = JWT.create()
                .withSubject(userDetailsImpl.getUsername()) // token 이름
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.REFRESH_EXPIRATION_TIME)) // token 만료 시간 : 현재시간 + 유효시간
                .withIssuedAt(new Date(System.currentTimeMillis())) // 발급 시간
                .sign(Algorithm.HMAC512(JwtProperties.SECRET2)); // secret key

        // Refresh Token DB에 저장
        memberService.updateRefreshToken(userDetailsImpl.getUsername(), refreshToken);

        // token 을 Header 에 발급
        // 재발급떼문에 setHeader 사용
        response.setHeader(JwtProperties.HEADER_ACCESS, JwtProperties.TOKEN_PREFIX + accessToken);
        response.setHeader(JwtProperties.HEADER_REFRESH, JwtProperties.TOKEN_PREFIX + refreshToken);
    }

}
