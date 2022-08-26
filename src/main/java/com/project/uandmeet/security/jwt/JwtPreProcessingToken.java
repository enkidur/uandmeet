package com.project.uandmeet.security.jwt;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

//자체 로그인
public class JwtPreProcessingToken extends UsernamePasswordAuthenticationToken {

    private JwtPreProcessingToken(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public JwtPreProcessingToken(String token) {
        this(token, token.length());
    }
}
