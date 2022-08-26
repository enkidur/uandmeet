package com.project.uandmeet.security.filter;

import com.project.uandmeet.exception.CustomException;
import com.project.uandmeet.exception.ErrorCode;
import com.project.uandmeet.security.jwt.HeaderTokenExtractor;
import com.project.uandmeet.security.jwt.JwtPreProcessingToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthFilter extends AbstractAuthenticationProcessingFilter {

    private final HeaderTokenExtractor extractor;

    public JwtAuthFilter(
            RequestMatcher requiresAuthenticationRequestMatcher,
            HeaderTokenExtractor extractor
    ) {
        super(requiresAuthenticationRequestMatcher);

        this.extractor = extractor;
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws AuthenticationException, IOException {
        try {
            // JWT 값을 담아주는 변수 TokenPayload
            String tokenPayload = request.getHeader("Authorization");
            if (tokenPayload == null) {
                throw new CustomException(ErrorCode.MEMBER_PASSWORD_NOT_FOUND);
                //로그인 페이지로 넘겨준다.
                //response.sendRedirect("/user/loginView");
            }

            JwtPreProcessingToken jwtToken = new JwtPreProcessingToken(
                    extractor.extract(tokenPayload, request));

            return super
                    .getAuthenticationManager()
                    .authenticate(jwtToken);
        } catch (CustomException e) {
            //response.sendError(e.getErrorCode().getHttpStatus().value(), e.getMessage());
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(e.getErrorCode().getHttpStatus().value());
            response.getWriter().println("{");
            response.getWriter().println("\"status\" : \"" + e.getErrorCode().getHttpStatus().value()+"\",");
            response.getWriter().println("\"errors\" : \"" + e.getErrorCode().getHttpStatus().name()+"\",");
            response.getWriter().println("\"code\" : \"" + e.getErrorCode().name()+"\",");
            response.getWriter().println("\"message\" : \"" + e.getErrorCode().getErrorMessage()+"\"");
            response.getWriter().println("}");
            return null;
        }
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult
    ) throws IOException, ServletException {
        /*
         *  SecurityContext 사용자 Token 저장소를 생성합니다.
         *  SecurityContext 에 사용자의 인증된 Token 값을 저장합니다.
         */
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authResult);
        SecurityContextHolder.setContext(context);

        // FilterChain chain 해당 필터가 실행 후 다른 필터도 실행할 수 있도록 연결실켜주는 메서드
        chain.doFilter(
                request,
                response
        );
    }

    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException failed
    ) throws IOException, ServletException {
        /*
         *	로그인을 한 상태에서 Token값을 주고받는 상황에서 잘못된 Token값이라면
         *	인증이 성공하지 못한 단계 이기 때문에 잘못된 Token값을 제거합니다.
         *	모든 인증받은 Context 값이 삭제 됩니다.
         */
        SecurityContextHolder.clearContext();

        super.unsuccessfulAuthentication(
                request,
                response,
                failed
        );
    }
}
