package com.project.uandmeet.repository;

import com.project.uandmeet.model.Guarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GuareaRepostiory extends JpaRepository<Guarea, Long> {
    Optional<Guarea> findBySigKorNmAndSiarea(String SigKorNm, Long id);
}
