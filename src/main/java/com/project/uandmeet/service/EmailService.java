package com.project.uandmeet.service;

import com.project.uandmeet.dto.CheckAuthNumDto;
import com.project.uandmeet.model.Member;
import com.project.uandmeet.redis.RedisUtil;
import com.project.uandmeet.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailService {
    @Autowired
    private JavaMailSenderImpl mailSender; //Application 에서 빈 등록했기 때문에 주입받을 수 있다.

    private final MemberRepository memberRepository;
    @Autowired
    private RedisUtil redisUtil;
    private String authNumber; // 난수 번호
    private int emailCnt; // email 인증 횟수
    //
    public void makeRandomNumber() {
//        // 난수의 범위 111111 ~ 999999 (6자리 난수)
//        Random r = new Random();
//        int checkNum = r.nextInt(888888) + 111111;
        String checkNum = UUID.randomUUID().toString().substring(0, 6);
        System.out.println("인증번호 : " + checkNum);
        authNumber = checkNum;
    }

    public String checkAuthNum(String authNum) {
        return String.valueOf(authNum.equals(authNumber));
    }


    //이메일 보낼 양식
    public String joinEmail(String email) throws IOException { // 컨트롤러에서 아이디가 넘어오면서 붙을 스트링값

        String[] emailadress = email.split("@");
        String id = emailadress[0];
        String host = emailadress[1];
//        String pattern = "^[a-zA-Z0-9]*$";
//        String pattern = "^[a-zA-Z0-9_!#$%&'\\*+/=?{|}~^.-]+@[a-zA-Z0-9.-]+.[a-zA-Z0-9.-]*$";
//        String idpattern = "^[a-zA-Z0-9_!#$%&'\\*+/=?{|}~^.-]*$";
//        String hostpattern = "^[a-zA-Z0-9.-]*$";
//        // email 조건
//        // ID 영문 대소문자, 숫자, _!#$%&'\*+/=?{|}~^.- 특문허용
//        // Host 시작전 @, 영문 대소문자, 숫자, .-특문허용
//
//        // 회원가입 username 조건
//        if (username.length() < 10) {
//            throw new IllegalArgumentException("이메일을 10자 이상 입력하세요");
//        } else if (!Pattern.matches(idpattern, id)) {
//            throw new IllegalArgumentException("id에 알파벳 대소문자와 숫자, 특수기호( _!#$%&'\\*+/=?{|}~^.-)로만 입력하세요");
//        } else if (!Pattern.matches(hostpattern, host)) {
//            throw new IllegalArgumentException("host에 알파벳 대소문자와 숫자, 특수기호(.-)로만 입력하세요");
//        } else if (!Pattern.matches(pattern, username)) {
//            throw new IllegalArgumentException("이메일 규격에 맞게 입력하세요");
//        } else if (username.contains("script")) {
//            throw new IllegalArgumentException("xss공격 멈춰주세요.");
//        }

        // email 중복 확인
        checkDuplicateEmail(email);

        if (emailCnt < 4) {
            makeRandomNumber();
            //인증메일 보내기
            String setFrom = "zkraudcka@naver.com"; // email-config에 설정한 자신의 이메일 주소를 입력
            String toMail = email;
            String title = "회원 가입 인증 이메일 입니다."; // 이메일 제목
            String content =
                    "홈페이지를 방문해주셔서 감사합니다." +    //html 형식으로 작성 !
                            "<br><br>" +
                            "인증 번호는 " + authNumber + "입니다." +
                            "<br>" +
                            "해당 인증번호를 인증번호 확인란에 기입하여 주세요."; //이메일 내용 삽입
            mailSend(setFrom, toMail, title, content);
            emailCnt += 1;
            int restCnt = 3 - emailCnt;
            return "인증 번호 :" + authNumber + "남은 횟수 :"+ restCnt;
        }
        return "인증 횟수를 초과하였습니다. 1시간 뒤에 다시 시도해 주세요.";
    }

    public void checkDuplicateEmail(String username) {
        Optional<Member> member = memberRepository.findByUsername(username);
        if (member.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 계정입니다.");
        }
    }

        //이메일 전송 메소드
        public void mailSend(String setFrom, String toMail, String title, String content) {
            MimeMessage message = mailSender.createMimeMessage();
            // true 매개값을 전달하면 multipart 형식의 메세지 전달이 가능.문자 인코딩 설정도 가능하다.
            try {
                MimeMessageHelper helper = new MimeMessageHelper(message,true,"utf-8");
                helper.setFrom(setFrom);
                helper.setTo(toMail);
                helper.setSubject(title);
                // true 전달 > html 형식으로 전송 , 작성하지 않으면 단순 텍스트로 전달.
                helper.setText(content,true);
                mailSender.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
            }

            // 유효 시간(3분)동안 {fromEmail, authKey} 저장
            redisUtil.setDataExpire(authNumber, setFrom, 60 * 3L);
            // 횟수
            redisUtil.setDataExpire("Cnt" + authNumber, String.valueOf(emailCnt),60 * 60L);
            // 유효 시간(1시간)동안 {toEmail, emailCnt} 저장
            redisUtil.setDataExpire(toMail, String.valueOf(emailCnt),60 * 60L);
        }
    }