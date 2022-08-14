package com.project.uandmeet.auth;

import com.project.uandmeet.model.Member;
import com.project.uandmeet.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

// security 설정에서 loginProcessingUrl("/login") 때문에
// login 요청이 오면 자동으로 UserDetailsService 타입으로 IoC 되어 있는 PrincipalDetailsService 의 loadUserByUsername 함수가 실행
// 즉, /login 요청이 오면 동작 => but SecurityConfig 에서와 같이 .fromLogin().disalbe() 하면 작동안함 -> 로그인폼을 사용안하니까
// => 이럴 경우 JwtAuthenticationFilter 를 만들어 직접 구동
@Service // @Service 를 통해 PrincipalDetailsService 가 Ioc 에 등록
@RequiredArgsConstructor // final 생성자 자동 생성
public class UserDetailsServiceImpl implements UserDetailsService{

    private final MemberRepository memberRepository;

    // Security Session => Authentication type => UserDetails type
    // 함수가 실행되면서 fornt 에서 username 파마미터를 가지고 옴
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // front 에서 받아온 username 파라미터와 String username 의 이름 동일
        // 다를 시, 이름을 통일 시켜 주거나 fornt 파마미터를 securityConfig 의  configure에 .usernameParameter("")로 등록
        System.out.println("PrincipalDetailsService : 진입");
        Member member = memberRepository.findByEmail(email).orElseThrow(
                ()-> new RuntimeException("해당 사용자 없음")
        );

        // session.setAttribute("loginUser", user);
        return new UserDetailsImpl(member);
    }
}
