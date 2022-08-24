package com.project.uandmeet.repository;

import com.project.uandmeet.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
