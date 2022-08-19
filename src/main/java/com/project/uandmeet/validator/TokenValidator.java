package com.project.uandmeet.validator;

import com.project.uandmeet.exception.CustomException;
import com.project.uandmeet.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component // 선언하지 않으면 사용할 수 없다!!!!!
@RequiredArgsConstructor
public class TokenValidator {

    //userid와 토큰 비교
    public void memberIdCompareToken(Long memberid, Long tokenuserid) {
        if(!Objects.equals(memberid, tokenuserid)){
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }
    }
}
