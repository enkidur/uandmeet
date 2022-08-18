package com.project.uandmeet.oauth;

import com.project.uandmeet.auth.UserDetailsImpl;
import com.project.uandmeet.model.Member;
import com.project.uandmeet.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

//현재 서비스 클래스는 Securityconfig에서 불러와지고 있음
@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private MemberRepository memberRepository;

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
        String loginto = userRequest.getClientRegistration().getClientId(); // google
        String role = "ROLE_USER";

        Member memberEntity = memberRepository.findByUsername(email).orElseThrow(
                ()-> new RuntimeException("해당 사용자 없음"));

        if(memberEntity == null){
            memberEntity = Member.builder()
                    .username(email)
                    .loginto(loginto)
                    .build();
            memberRepository.save(memberEntity);
        }

        // 회원가입을 강제로 진행해볼 예정
        return new UserDetailsImpl(memberEntity, oAuth2User.getAttributes());  // 리턴될때 authenticatino에 저장됨
    }

}
