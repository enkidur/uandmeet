package com.project.uandmeet.security;



import com.project.uandmeet.model.Member;
import com.project.uandmeet.repository.MemberRepostiory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserDetailsServiceImpl implements UserDetailsService{

    private final MemberRepostiory memberRepostiory;
    @Autowired
    public UserDetailsServiceImpl(MemberRepostiory memberRepostiory) {
        this.memberRepostiory = memberRepostiory;
    }

    public UserDetailsImpl loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepostiory.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Can't find " + username));

        return new UserDetailsImpl(member);
    }
}