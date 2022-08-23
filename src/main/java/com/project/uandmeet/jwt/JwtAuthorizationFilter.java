package com.project.uandmeet.jwt;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.project.uandmeet.auth.UserDetailsImpl;
import com.project.uandmeet.model.Member;
import com.project.uandmeet.repository.MemberRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

// 인가
// Security Filter 의 BasicAuthenticationFilter 는 상시 실행되나
// 권한이나 인증이 필요한 주소를 요청했을 때 token 의 유무를 검사
public class JwtAuthorizationFilter extends BasicAuthenticationFilter{

    private MemberRepository memberRepository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, MemberRepository memberRepository) {
        super(authenticationManager);
        this.memberRepository = memberRepository;
    }

    // 인증이나 권한이 필요한 요청이 있을 때 해당 filter 를 실행
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        // 로그인, 리프레시 요청이라면 토큰 검사하지 않음
        if (request.getServletPath().equals("/login") || request.getServletPath().equals("/refresh")) {
            chain.doFilter(request, response);
        }

        if(header == null || !header.startsWith(JwtProperties.TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }
        System.out.println("header : "+header);
        String token = request.getHeader(HttpHeaders.AUTHORIZATION)
                .replace(JwtProperties.TOKEN_PREFIX, "");

        // 토큰 검증 (이게 인증이기 때문에 AuthenticationManager도 필요 없음)
        // 내가 SecurityContext에 집적접근해서 세션을 만들때 자동으로 UserDetailsService에 있는 loadByUsername이 호출됨.
        String username = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(token)
                .getSubject();

        if(username != null) {
            Member member = memberRepository.findByUsername(username).orElseThrow(
                    ()-> new RuntimeException("해당 사용자가 없습니다."));

            // 인증은 토큰 검증시 끝. 인증을 하기 위해서가 아닌 스프링 시큐리티가 수행해주는 권한 처리를 위해
            // 아래와 같이 토큰을 만들어서 Authentication 객체를 강제로 만들고 그걸 세션에 저장!
            UserDetailsImpl userDetailsImpl = new UserDetailsImpl(member);
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetailsImpl, //나중에 컨트롤러에서 DI해서 쓸 때 사용하기 편함.
                            null, // 패스워드는 모르니까 null 처리, 어차피 지금 인증하는게 아니니까!!
                            userDetailsImpl.getAuthorities());

            // security 를 저장할 수 있는 session 공간
            // 강제로 시큐리티의 세션에 접근하여 값 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }

}
