package com.project.uandmeet.repository;

import com.project.uandmeet.model.Member;
import com.project.uandmeet.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findAllById(Long memberId);
    Long countByTo(Member to);
    Long countByToAndNum(Member to, int i);
    List<Review> findByTo(Member to);
}