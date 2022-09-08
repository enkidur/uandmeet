package com.project.uandmeet.oauth;

import com.project.uandmeet.redis.RedisUtil;
import com.project.uandmeet.security.UserDetailsImpl;
import com.project.uandmeet.security.jwt.JwtProperties;
import com.project.uandmeet.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    private final RedisUtil redisUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        System.out.println("------------------onAuthentication_Success를 거침-------------------------");

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal(); // 구글 이메일

        String nickname = userDetails.getMember().getNickname();

        String username = userDetails.getUsername();

        String profile = userDetails.getMember().getProfile();

        String accessToken = jwtTokenProvider.createToken(userDetails.getUsername(), userDetails.getMember().getId());

        String refreshToken = jwtTokenProvider.createRefreshToken(userDetails.getUsername());

        // redis 에 token 저장
        redisUtil.setDataExpire(userDetails.getUsername()+JwtProperties.HEADER_ACCESS, JwtProperties.TOKEN_PREFIX + accessToken, JwtProperties.ACCESS_EXPIRATION_TIME);
        redisUtil.setDataExpire(userDetails.getUsername()+JwtProperties.HEADER_REFRESH, JwtProperties.TOKEN_PREFIX + refreshToken, JwtProperties.REFRESH_EXPIRATION_TIME);


        String url = makeRedirectUrl(accessToken, refreshToken,username , nickname, profile);

        System.out.println("조합된 URL: "+url);

        getRedirectStrategy().sendRedirect(request, response, url);
    }

    private String makeRedirectUrl(String access_Token, String refresh_Token,String username,String nickname, String profile) {
        return UriComponentsBuilder.fromUriString("http://localhost:3000/oauth2/redirect")
                .queryParam("access_Token", access_Token)
                .queryParam("refresh_Token", refresh_Token)
                .queryParam("username", username)
                .queryParam("nickname", nickname)
                .queryParam("profile", profile)
                .build().toUriString();
    }
}
