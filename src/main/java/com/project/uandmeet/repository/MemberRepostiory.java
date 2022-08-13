package com.project.uandmeet.repository;

import com.project.uandmeet.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public  interface MemberRepostiory extends JpaRepository<Member, Long> {

}
