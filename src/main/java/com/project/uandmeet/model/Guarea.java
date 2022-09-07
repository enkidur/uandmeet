package com.project.uandmeet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.simple.JSONObject;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
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

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "gu",cascade = CascadeType.ALL)
    private List<Board> boardList = new ArrayList<>();

    public Guarea(JSONObject jsonProperties,
                  JSONObject jsonPropertiesProperties, Siarea siareatemp)
    {
        this.Info = (String) jsonProperties.get("id");
        this.sigCd =(String) jsonPropertiesProperties.get("sig_cd");
        this.fullNm = (String) jsonPropertiesProperties.get("full_nm");
        this.sigEngNm = (String) jsonPropertiesProperties.get("sig_eng_nm");
        this.sigKorNm = (String) jsonPropertiesProperties.get("sig_kor_nm");
        this.siarea = siareatemp;
    }
}
