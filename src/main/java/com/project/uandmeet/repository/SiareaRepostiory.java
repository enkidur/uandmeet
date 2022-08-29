package com.project.uandmeet.repository;

import com.project.uandmeet.model.Siarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SiareaRepostiory extends JpaRepository<Siarea, Long> {

    Optional<Siarea> findByCtpKorNmAbbreviation(String CtpKorNmAbbreviation);
}
