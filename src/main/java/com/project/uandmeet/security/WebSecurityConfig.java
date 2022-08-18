package com.project.uandmeet.security;

//1. 코드받기 (인증)  2. 액세스토큰(권한)
//  3. 사용자프로필 정보를 가져와서 4-1. 그 정보를 토대로 회원가입을 자동으로 진행시키기도 함
// 4-2 (이메일, 전화번호,이름,아이디) 쇼핑몰 -> (집주소), 백화점몰 -> (vip등급, 일반등급)
import com.project.uandmeet.jwt.JwtAuthenticationFilter;
import com.project.uandmeet.jwt.JwtAuthorizationFilter;
import com.project.uandmeet.oauth.PrincipalOauth2UserService;
import com.project.uandmeet.redis.RedisUtil;
import com.project.uandmeet.repository.MemberRepository;
import com.project.uandmeet.service.MemberService;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity // 시큐리티 활성화 -> 기본 스프링 필터체인에 등록
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{

    @Autowired
    private PrincipalOauth2UserService principalOauth2UserService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CorsConfig corsConfig;

    @Autowired
//    private MemberService memberService;
    private RedisUtil redisUtil;
    @Override
    public void configure(WebSecurity web) {
// h2-console 사용에 대한 허용 (CSRF, FrameOptions 무시)
        web
                .ignoring()
                .antMatchers("/h2-console/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Security Filter에 필터 등록해야한다면 Security Filter만 등록 가능하기 때문에 Security Filter 시작 전 or 후에 등록
        // Security Filter가 우선 실행 후(Before, After 상관없이) 나머지 MyFilter실행
//        http.addFilterBefore(new MyFilter3(), BasicAuthenticationFilter.class); // Filter.class는 chainFilter 참고
        // jwt 고정 사용
        http	 // @CrossOrign : 인증 X, securityFilter 에 등록 : 인증 O
                    .addFilter(corsConfig.corsFilter()) // 모든 요청은 해당 필터를 타게 되서 cors 정책에서 벗어날 수 있음
                    .csrf().disable() // csrf 가 켜져 있으면 form 태그로 요청시에 csrf 속성이 추가됩니다. 서버쪽에서 만들어준 form 태그로만 요청이 가능
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // session 사용 X (STATELESS 서버)
                .and()
                    .formLogin().disable() // formLogin 안씀
//                     Header 의 Authorization key value 에 인증 정보(id, pw)를 담아서 요청하는 방식 -> cookie, session 필요 X
                    .httpBasic().disable() // 확장성은 좋지만 암호화가 안되서 보안 취약하기 때문에 https 서버 사용하여 암호화
//
                    .addFilter(new JwtAuthenticationFilter(authenticationManager(),redisUtil)) // AuthenticatonManager 파라미터 필요
                    .addFilter(new JwtAuthorizationFilter(authenticationManager(), memberRepository))// AuthenticatonManager 파라미터 필요
                    // token을 사용하는 형식은 Bearer -> 노출되도 특정 시간 뒤 파기되기 때문에 인증 정보를 그대로 노출하는 것보단 높은 안정성
                    .authorizeRequests()
//                    .antMatchers("/api/user/**")
//                    .access("hasRole('ROLE_USER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
//                    .antMatchers("/api/manager/**")
//                    .access("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
//                    .antMatchers("/api/admin/**")
//                    .access("hasRole('ROLE_ADMIN')")
                    .anyRequest().permitAll()
//                .and()
//        http.authorizeRequests()
//                .antMatchers("/user/**").authenticated()
//                //.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
//                //.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN') and hasRole('ROLE_USER')")
//                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
//                .anyRequest().permitAll()
//                .and()
//                .formLogin() // 인증이 필요하면 무조건 formLogin()으로 오게 되어있음
//                .loginPage("/api/loginForm")
                .and()
                .oauth2Login()
                .loginPage("/api/loginForm")
                .userInfoEndpoint()
                .userService(principalOauth2UserService);// 구글 로그인이 완료된 뒤의 후처리가 필요함. Tip. 코드로 받는게아니고 엑세스토큰+사용자 프로필 정보를 한방에 받게됨
        }
}

