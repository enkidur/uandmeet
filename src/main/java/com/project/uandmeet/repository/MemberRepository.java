package com.project.uandmeet.repository;

import com.project.uandmeet.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {
//    Member findByUsername(String username);

    Optional<Member> findByUsername(String username);
    Optional<Member> findByEmail(String email);
}
