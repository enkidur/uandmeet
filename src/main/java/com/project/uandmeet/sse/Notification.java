package com.project.uandmeet.sse;

import com.project.uandmeet.model.BaseTime;
import com.project.uandmeet.model.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String content;
    //알림 내용 - 50자 이내
    @Column(nullable = false)
    private Boolean isRead;
    //읽었는지에 대한 여부


    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member receiver;

    public void updateRead(){
        isRead = true;
    }
}