package com.project.uandmeet.service;

import com.project.uandmeet.dto.*;
import com.project.uandmeet.dto.request.*;
import com.project.uandmeet.dto.response.SignupResponseDto;
import com.project.uandmeet.exception.CustomException;
import com.project.uandmeet.exception.ErrorCode;
import com.project.uandmeet.model.Member;
import com.project.uandmeet.redis.RedisUtil;
import com.project.uandmeet.repository.MemberRepository;
import com.project.uandmeet.security.UserDetailsImpl;
import com.project.uandmeet.security.jwt.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
public class MemberService {

    public static final String AUTH_HEADER = "Authorization";

    public static final String TOKEN_TYPE = "BEARER";

    private final MemberRepository memberRepository;
    private final RedisUtil redisUtil;
    private final PasswordEncoder passwordEncoder;

    // TODO: 2022-08-26 이메일 코드인증 구현필요

    // 비밀번호 생성 및 유저네임 유효성검사 회원가입
    public ResponseEntity signup(SignupRequestDto signupRequestDto) {
        String username = signupRequestDto.getUsername();
        String usernamePattern = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
        String pattern = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^*+=-]).{6,12}$";
        String password = signupRequestDto.getPassword();
        String passwordCheck = signupRequestDto.getPasswordCheck();

        // 아아디 정규식 맞지않는 경우 오류메세지를 전달해준다.
        if(username.equals("")) {
            throw new CustomException(ErrorCode.EMPTY_USERNAME);
        } else if(!Pattern.matches(usernamePattern, username)) {
            throw new CustomException(ErrorCode.USERNAME_WRONG);
        } else if(memberRepository.findByUsername(username).isPresent()){
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }else if(signupRequestDto.getPassword().equals("")){
            throw new CustomException(ErrorCode.EMPTY_PASSWORD);
        }else if( 6 > password.length() || 12 < password.length()){
            throw new CustomException(ErrorCode.PASSWORD_LEGNTH);
        }else if(!Pattern.matches(pattern,password)){
            throw new CustomException(ErrorCode.PASSWORD_WRONG);
        } else if(!signupRequestDto.getPassword().equals(passwordCheck)){
            throw new CustomException(ErrorCode.PASSWORD_CHECK);
        }
        password = passwordEncoder.encode(password);


        //member 객체에 requestDto에서 받아온값을 넣는다.
        Member member = new Member(username, password);
        //member 객체를 저장한다.
        memberRepository.save(member);

        member.setNickname(member.getNickname()+ " " + member.getId());
        memberRepository.save(member);
        SignupResponseDto signupResponseDto = new SignupResponseDto(member,"회원가입 성공");
        return new ResponseEntity(signupResponseDto, HttpStatus.OK);
    }

    // 마이페이지, 회원가입 닉네임 중복 체크
    public ResponseEntity nicknameCheck(NicknameRequestDto nicknameRequestDto) {
        String nickname = nicknameRequestDto.getNickname();
        if(nickname.equals("")) {
            throw new CustomException(ErrorCode.EMPTY_NICKNAME);
        } else if(nickname.length() < 2 || nickname.length() > 10) {
            throw new CustomException(ErrorCode.NICKNAME_LEGNTH);
        } else if (memberRepository.findByNickname(nickname).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }
        return new ResponseEntity("사용가능한 닉네임입니다", HttpStatus.OK);
    }


    // 마이페이지, 회원가입 닉네임 수정
    public ResponseEntity nickname(Long memberId, NicknameRequestDto nicknameRequestDto, HttpServletResponse response) {
        String nickname = nicknameRequestDto.getNickname();
        Member member1 = memberRepository.findById(memberId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));
        member1.setNickname(nickname);
        memberRepository.save(member1);

        String token = JwtTokenUtils.generaterefreshToken(member1);
        response.addHeader(AUTH_HEADER, TOKEN_TYPE + " " + token);

        return new ResponseEntity("닉네임 저장완료", HttpStatus.OK);
    }

    // TODO: 2022-08-26 마이페이지 -> 이미지 업로드 기능 구현 필요

    //마이페이지 비밀번호 일치 확인 후 비밀번호 변경 모달창으로 이동
    public ResponseEntity passwordCheck(UserDetailsImpl userDetails, PasswordCheckRequestDto passwordCheckRequestDto) {
        String password = passwordCheckRequestDto.getPassword();
        if(password.equals("")) {
            throw new CustomException(ErrorCode.EMPTY_PASSWORD);
        } else if(!passwordEncoder.matches(password, userDetails.getPassword())){
            throw new CustomException(ErrorCode.PASSWORD_CHECK);
        }
        return new ResponseEntity("비밀번호 일치", HttpStatus.OK);
    }

    // 마이페이지 비밀번호 변경
    public ResponseEntity passwordChange(UserDetailsImpl userDetails, PasswordRequestDto passwordRequestDto) {
        String password = passwordRequestDto.getPassword();
        String passwordCheck = passwordRequestDto.getPasswordCheck();
        if(userDetails == null){
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        } else if(password.equals("")) {
            throw new CustomException(ErrorCode.EMPTY_PASSWORD);
        } else if(!password.equals(passwordCheck)) {
            throw new CustomException(ErrorCode.PASSWORD_CHECK);
        } else if(password.length() < 6 || password.length() > 12) {
            throw new CustomException(ErrorCode.PASSWORD_LEGNTH);
        } else if(!Pattern.matches("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^*+=-]).{6,12}$", password)) {
            throw new CustomException(ErrorCode.PASSWORD_WRONG);
        }

        password = passwordEncoder.encode(password);
        Member member = memberRepository.findMemberById(userDetails.getMember().getId());
        member.setPassword(password);
        memberRepository.save(member);
        return new ResponseEntity("비밀번호 변경 완료", HttpStatus.OK);
    }

    public ResponseEntity findPasswordChange(FindPasswordRequestDto findPasswordRequestDto) {
        String username = findPasswordRequestDto.getUsername();
        Member member = memberRepository.findMemberByUsername(username);
        String password = findPasswordRequestDto.getPassword();
        String passwordCheck = findPasswordRequestDto.getPasswordCheck();
        if(password.equals("")) {
            throw new CustomException(ErrorCode.EMPTY_PASSWORD);
        } else if(!password.equals(passwordCheck)) {
            throw new CustomException(ErrorCode.PASSWORD_CHECK);
        } else if(password.length() < 6 || password.length() > 12) {
            throw new CustomException(ErrorCode.PASSWORD_LEGNTH);
        } else if(!Pattern.matches("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^*+=-]).{6,12}$", password)) {
            throw new CustomException(ErrorCode.PASSWORD_WRONG);
        }
        password = passwordEncoder.encode(password);
        member.setPassword(password);
        memberRepository.save(member);
        return new ResponseEntity("비밀번호 변경 완료", HttpStatus.OK);
    }
}

