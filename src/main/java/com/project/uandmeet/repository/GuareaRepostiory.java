package com.project.uandmeet.repository;

import com.project.uandmeet.model.Guarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuareaRepostiory extends JpaRepository<Guarea, Long> {

}
