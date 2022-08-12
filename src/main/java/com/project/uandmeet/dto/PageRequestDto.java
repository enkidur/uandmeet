package com.project.uandmeet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


@Data
@AllArgsConstructor
public class PageRequestDto { // 화면에서 전달되는 page,size 파라미터를 수집하는 역할
                                // JPA쪽에서 사용하는 Pageable타입의 객체를 생성
    private int page;
    private int size;

    public PageRequestDto(){
        this.page = 1;
        this.size = 10;
    }
    public Pageable getPageable(Sort sort){
        return PageRequest.of(page -1, size , sort);
        //JPA를 이용하는 경우 페이지 번호가 0부터 시작한다는 점을 감안하여 1페이지의 경우 0이 될 수 있도록 page-1형태로 작성
    }

}
