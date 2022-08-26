package com.project.uandmeet.security;

import com.project.uandmeet.security.jwt.JwtTokenUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FormLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    public static final String AUTH_HEADER = "Authorization";
    public static final String REFRESH_HEADER = "RefreshToken";
    public static final String TOKEN_TYPE = "BEARER";


    //성공시 응답에 토큰을 추가하는 핸들러
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) {
        final UserDetailsImpl userDetails = ((UserDetailsImpl) authentication.getPrincipal());

        final String token = JwtTokenUtils.generateJwtToken(userDetails);
//        final String refreshToken = JwtTokenUtils.generaterefreshToken(user);

        response.addHeader(AUTH_HEADER, TOKEN_TYPE + " " + token);

//        //User nicakname 내려주기 - 동관 천재님꺼 참고
//        response.setContentType("application/json");
//        User user = userDetails.getUser();
//        LoginResponseDto loginResponseDto = new LoginResponseDto(user.getNickname(), true, token);
//        String result = mapper.writeValueAsString(loginResponseDto);
//        response.getWriter().write(result);
    }
}
