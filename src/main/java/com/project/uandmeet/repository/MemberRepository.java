package com.project.uandmeet.repository;

import com.project.uandmeet.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUsername(String username);

    @Query(nativeQuery = true, value = "select * from member ur where ur.social =:social and ur.email =:email")
    Optional<Member> findByEmailAndSocial(String email,String social);

    Optional<Member> findByEmail(String email);

    Optional<Member> findByNickname(String nickname);

}
