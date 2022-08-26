package com.project.uandmeet.dto.ApiDtoGroup;

import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

@Getter
@Setter
public class GuareaDto {
    //전체명
    private String fullNm;
    //한글명
    private String sigKorNm;
    //영문명
    private String sigEngNm;
    //고유번호
    private String sigCd;

    //시도번호
    private String Info;

    public GuareaDto(JSONObject jsonProperties,
                     JSONObject jsonPropertiesProperties)
    {
        this.Info = (String) jsonProperties.get("id");
        this.sigCd =(String) jsonPropertiesProperties.get("sig_cd");
        this.fullNm = (String) jsonPropertiesProperties.get("full_nm");
        this.sigEngNm = (String) jsonPropertiesProperties.get("sig_eng_nm");
        this.sigKorNm = (String) jsonPropertiesProperties.get("sig_kor_nm");
    }
}
