package com.project.uandmeet.repository;

import com.project.uandmeet.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);

    Optional<Member> findByNickname(String nickname);

    Optional<Member> findById(Long memberId);
    void deleteByUsername(String username);


}
