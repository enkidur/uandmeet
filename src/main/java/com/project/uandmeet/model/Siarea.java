package com.project.uandmeet.model;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.simple.JSONObject;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Siarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //영문명
    private String ctpEngNm;

    //고유번호
    private String ctpRvnCd;

    //한글명
    private String ctpKorNm;

    //시도번호
    private String Info;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "siarea",cascade = CascadeType.ALL)
    private List<Guarea> guareas = new ArrayList<>();
    public Siarea(JSONObject jsonProperties,
                  JSONObject jsonPropertiesProperties)
    {
        this.Info = (String) jsonProperties.get("id");
        this.ctpEngNm = (String) jsonPropertiesProperties.get("ctp_eng_nm");
        this.ctpRvnCd = (String) jsonPropertiesProperties.get("ctprvn_cd");
        this.ctpKorNm = (String) jsonPropertiesProperties.get("ctp_kor_nm");
    }
}
