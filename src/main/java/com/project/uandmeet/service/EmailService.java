package com.project.uandmeet.service;

import com.project.uandmeet.exception.CustomException;
import com.project.uandmeet.exception.ErrorCode;
import com.project.uandmeet.model.Member;
import com.project.uandmeet.redis.RedisUtil;
import com.project.uandmeet.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailService {
    @Autowired
    private JavaMailSenderImpl mailSender; //Application 에서 빈 등록했기 때문에 주입받을 수 있다.

    @Autowired
    private RedisUtil redisUtil;
    //    private String authNumber; // 난수 번호
    private int emailCnt = 0; // email 인증 횟수
    //
    public String makeRandomNumber() {
//        // 난수의 범위 111111 ~ 999999 (6자리 난수)
//        Random r = new Random();
//        int checkNum = r.nextInt(888888) + 111111;
        String checkNum = UUID.randomUUID().toString().substring(0, 6);
        System.out.println("인증번호 : " + checkNum);
        return checkNum;
    }



    //이메일 보낼 양식
    @Transactional
    public String joinEmail(String email) { // 컨트롤러에서 아이디가 넘어오면서 붙을 스트링값
        redisUtil.setDataExpire("Auth" + email, makeRandomNumber(),60 * 3L);
        redisUtil.setDataExpire("Cnt" + email, String.valueOf(emailCnt),60 * 60L);
        if (Integer.parseInt(redisUtil.getData("Cnt" + email)) < 4) {
            redisUtil.setDataExpire("Cnt" + email, String.valueOf(emailCnt + 1),60 * 60L);
            int restCnt = 3 - Integer.parseInt(redisUtil.getData("Cnt" + email));
            //인증메일 보내기
            String setFrom = "wkraudcka@naver.com"; // email-config에 설정한 자신의 이메일 주소를 입력
            String toMail = email;
            String title = "회원 가입 인증 이메일 입니다."; // 이메일 제목
            String content =
                    " <div" 																																																	+
                            "	style=\"font-family: 'Apple SD Gothic Neo', 'sans-serif' !important; width: 400px; height: 600px; border-top: 4px solid #00CFFF; margin: 100px auto; padding: 30px 0; box-sizing: border-box;\">"		+
                            "	<h1 style=\"margin: 0; padding: 0 5px; font-size: 28px; font-weight: 400;\">"																															+
                            "		<span style=\"font-size: 15px; margin: 0 0 10px 3px;\">너나만나</span><br />"																													+
                            "		<span style=\"color: #00CFFF\">메일인증</span> 안내입니다."																																				+
                            "	</h1>\n"																																																+
                            "	<p style=\"font-size: 16px; line-height: 26px; margin-top: 50px; padding: 0 5px;\">"																													+
                            toMail																																																+
                            "		님 안녕하세요.<br />"																																													+
                            "		너나만나에 가입해 주셔서 진심으로 감사드립니다.<br />"																																						+
                            "		아래 <b style=\"color: #00CFFF\">'인증 번호'</b> 를 입력하여 회원가입을 완료해 주세요.<br />"																													+
                            "		감사합니다."																																															+
                            "	</p>"																																																	+
                            "          <div style=\"text-align: center;\"><h1><b style=\"color: #00CFFF\" >" + redisUtil.getData("Auth" + email) + "<br /><h1></div>"																																										+
                            "	<div style=\"border-top: 1px solid #DDD; padding: 5px;\"></div>"																																		+
                            "<br>" +
                            "남은 인증 횟수 : " + restCnt +
                            " </div>";

            mailSend(setFrom, toMail, title, content);
            return "남은 횟수 :"+ restCnt;
        }
        return "인증 횟수를 초과하였습니다. 1시간 뒤에 다시 시도해 주세요.";
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
    }

    public String checkAuthNum(String authNum, String email) {
        return String.valueOf(authNum.equals(redisUtil.getData("Auth" + email)));
    }

}