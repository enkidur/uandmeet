package com.project.uandmeet.model;

import lombok.*;

import javax.persistence.*;

/**
 * <h1>Concern<h1/>
 * <p>
 *     관심종목 엔티티
 * </p>
 * <p>
 *     계정별로 1~5개 등록할 수 있게 제한
 * </p>
 * <p>신규 관심종목 생성시 다음 메소드 참조 {@link #createConcern(String)}</p>
 */

@Setter
@Getter
@Entity
public class Concern {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONCERN_ID")
    private Long id;

    @Column(name = "CONCERN_NAME")
    private String name;

    @Column(name = "MEMBER_ID")
    private String  memberId;

    // 관심 종목 엔티티를 빌더나 생성자를 통해 무분별하게 생성하는 것을 방지
    @Builder(access = AccessLevel.PRIVATE)
    private Concern(String name) {
        this.name = name;
    }

    protected Concern(){

    }

    /**
     * 신규 관심종목 엔티티를 생성후 반환
     * @param name  관심종목 명
     * @return  concern  생성된 관심종목 엔티티
     */
    public static Concern createConcern(String name){
        return Concern.builder()
                .name(name)
                .build();
    }

}
