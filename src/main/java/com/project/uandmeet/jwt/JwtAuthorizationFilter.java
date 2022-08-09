package com.project.uandmeet.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.project.uandmeet.model.Member;
import com.project.uandmeet.repository.MemberRepostiory;
import com.project.uandmeet.security.UserDetailsImpl;
import org.hibernate.engine.spi.ManagedEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//시큐리티가 filter 가지고 있는데 그 필터중에 BasicAuthenticationFilter 라는 것이 있음.
//권한이나 인증이 필요한 특정 주소를 요청했을 때 위 필터를 무조건 타게 되어있음.
//만약 권한이나 인증이 필요한 주소가 아니라면 이 필터를 안탐.
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private MemberRepostiory memberRepostiory;
    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, MemberRepostiory memberRepostiory) {
        super(authenticationManager);
        this.memberRepostiory = memberRepostiory;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        System.out.println("인증이나 권한이 필요한 주소 요청이 됨");

        //해더에서 추출
        String jwtHeader = request.getHeader("Authorization");
        System.out.println("jwtHeader: "+ jwtHeader);

        //header가 있는지 확인
        if(jwtHeader == null) {
            chain.doFilter(request, response);
            return;
        }

        //JWT 토큰을 검증을 해서 정상적인 사용자인지 확인
        String username =
                JWT.require(Algorithm.HMAC512("cos")).build().verify(jwtHeader).getClaim("username").asString();

        //서명이 정상적으로 됨.
        if(username != null) {
            Member memberEntity = memberRepostiory.findByUsername(username).orElseThrow(
                    ()-> new IllegalArgumentException("username이 없습니다.")
            );

            UserDetailsImpl userDetails = new UserDetailsImpl(memberEntity);

            //Jwt 토큰 서명을 통해서 서명이 정상이면 Authentication 객체를 만들어 준다.
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null,userDetails.getAuthorities());

            //홀더에 값 넣어주는 것.
            SecurityContextHolder.getContext().setAuthentication(authentication);

            chain.doFilter(request, response);
        }

    }
}
