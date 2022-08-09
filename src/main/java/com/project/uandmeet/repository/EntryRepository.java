package com.project.uandmeet.repository;

import com.project.uandmeet.model.Board;
import com.project.uandmeet.model.Entry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.ManyToOne;
import java.util.List;

@Repository
public interface EntryRepository extends JpaRepository <Entry, Long> {

    @Transactional
    @Modifying // 쿼리를 통해 작성된 INSERT, UPDATE, DELETE(SELECT제외) 쿼리에서 사용되는 어노테이션
    @Query("delete FROM Entry e where e.member = :id")
    void deleteAllByidQuery(Long id);

    List<Board> findAllByBoardTypeAndCategory(String boardType, String categoryName);

}
