package com.project.uandmeet.repository;

import com.project.uandmeet.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);

    Optional<Member> findByNickname(String nickname);

    Member findMemberByNickname(String nickname);
    Member findMemberByUsername(String username);
    void deleteByUsername(String username);
    List<Member> findAllById(Long id);
}
