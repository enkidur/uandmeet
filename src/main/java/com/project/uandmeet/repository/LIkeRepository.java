

package com.project.uandmeet.repository;

import com.project.uandmeet.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LIkeRepository extends JpaRepository<Like, Long> {
    boolean existsByBoardAndMember(Long boardid, Long memberid);
    void deleteByBoardAndMember(Long boardid, Long memberid);
}

