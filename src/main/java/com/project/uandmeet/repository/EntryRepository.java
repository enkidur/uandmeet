package com.project.uandmeet.repository;

import com.project.uandmeet.model.Entry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntryRepository extends JpaRepository <Entry, Long> {
}
