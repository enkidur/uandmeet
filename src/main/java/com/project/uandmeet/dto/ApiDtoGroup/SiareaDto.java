package com.project.uandmeet.dto.ApiDtoGroup;

import com.project.uandmeet.api.nameChange;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

@Getter
@Setter
public class SiareaDto {

    //영문명
    private String ctpEngNm;

    //고유번호
    private String ctpRvnCd;

    //한글명
    private String ctpKorNm;

    //시도번호
    private String Info;

    //한글명 약어
    private String ctpKorNmAbbreviation;

    public SiareaDto(JSONObject jsonProperties,
                  JSONObject jsonPropertiesProperties)
    {
        this.Info = (String) jsonProperties.get("id");
        this.ctpEngNm = (String) jsonPropertiesProperties.get("ctp_eng_nm");
        this.ctpRvnCd = (String) jsonPropertiesProperties.get("ctprvn_cd");
        this.ctpKorNm = (String) jsonPropertiesProperties.get("ctp_kor_nm");
        this.ctpKorNmAbbreviation = nameChange.nameChange((String) jsonPropertiesProperties.get("ctp_kor_nm"));
    }
}
