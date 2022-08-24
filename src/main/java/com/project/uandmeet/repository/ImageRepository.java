package com.project.uandmeet.repository;

import com.project.uandmeet.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image,Long> {

    Image findByImageId(Long ImageId);
}
