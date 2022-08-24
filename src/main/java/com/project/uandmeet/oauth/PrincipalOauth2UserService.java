package com.project.uandmeet.oauth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.project.uandmeet.model.Member;
import com.project.uandmeet.redis.RedisUtil;
import com.project.uandmeet.repository.MemberRepository;
import com.project.uandmeet.security.UserDetailsImpl;
import com.project.uandmeet.security.jwt.JwtProperties;
import com.project.uandmeet.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

//현재 서비스 클래스는 Securityconfig에서 불러와지고 있음
@RequiredArgsConstructor
@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RedisUtil redisUtil;
    private final JwtTokenProvider jwtTokenProvider;

    //구글로 받은 userRequest 데이터에 대한 후처리되는 함수
    // 함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다.
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException { //userRequest에 정보가 전부 담겨있음
        System.out.println("userRequest : "+userRequest.getClientRegistration()); // registrationId로 어떤 OAuth로 로그인 했는지 알 수 있음
        System.out.println("userRequest : "+userRequest.getAccessToken().getTokenValue());

        OAuth2User oAuth2User = super.loadUser(userRequest);
        // 구글 로그인 버튼 클릭 -> 구글로그인 창 -> 로그인을 완료 -> code를 리턴(OAuth2-Client라이브러리) -> AccessToken 요청
        // 여기까지가 userRequest정보 -> loadUser함수 호출 -> 구글로부터 회원프로필 받아줌.
        System.out.println("userRequest : "+super.loadUser(userRequest).getAttributes()); // 구글 로그인한 유저의 이름,이메일,사진 등이 담겨있음

        String email = oAuth2User.getAttribute("email");
        String loginto ="Google";
        String role = "ROLE_USER";
        String password = UUID.randomUUID().toString();
        String encodedPassword = passwordEncoder.encode(password);

        Member memberEntity = memberRepository.findByUsername(email).orElse(null);


        if(memberEntity == null){
            memberEntity = Member.builder()
                    .username(email)
                    .loginto(loginto)
                    .password(encodedPassword)
                    .build();
            System.out.println("유저아이디"+memberEntity.getUsername());
            System.out.println("플랫폼"+memberEntity.getLoginto());
            System.out.println("패스워드"+memberEntity.getPassword());
            memberRepository.save(memberEntity);


            String accessToken = jwtTokenProvider.createToken(memberEntity.getUsername());
            String refreshToken = jwtTokenProvider.createRefreshToken();

            // redis 에 token 저장
            redisUtil.setDataExpire(memberEntity.getUsername(),refreshToken,JwtProperties.REFRESH_EXPIRATION_TIME);


            // token 을 Header 에 발급
            // 재발급떼문에 set 사용
            HttpHeaders headers = new HttpHeaders();
            headers.set(JwtProperties.HEADER_ACCESS, JwtProperties.TOKEN_PREFIX + accessToken);
            headers.set(JwtProperties.HEADER_REFRESH, JwtProperties.TOKEN_PREFIX + refreshToken);
        }

        return new UserDetailsImpl(memberEntity, oAuth2User.getAttributes());  // 리턴될때 authentication 에 저장됨
    }

}

