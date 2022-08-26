package com.project.uandmeet.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.simple.JSONObject;

import javax.persistence.*;

@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Guarea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "siarea_id")
    private Siarea siarea;

    public Guarea(JSONObject jsonProperties,
                     JSONObject jsonPropertiesProperties)
    {
        this.Info = (String) jsonProperties.get("id");
        this.sigCd =(String) jsonPropertiesProperties.get("sig_cd");
        this.fullNm = (String) jsonPropertiesProperties.get("full_nm");
        this.sigEngNm = (String) jsonPropertiesProperties.get("sig_eng_nm");
        this.sigKorNm = (String) jsonPropertiesProperties.get("sig_kor_nm");
    }
}
