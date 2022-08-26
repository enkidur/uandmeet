package com.project.uandmeet.security.jwt;

public interface JwtProperties {
    // 편의상
    String SECRET = "cos";
    String SECRET2 = "ace";
    int ACCESS_EXPIRATION_TIME = 1000 * 60 * 30 * 6 ;
    int REFRESH_EXPIRATION_TIME = 1000 * 60 * 60 * 30;
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_ACCESS = "AccessToken";
    String HEADER_REFRESH = "RefreshToken";

    String CLAIM_EXPIRED_DATE = "EXPIRED_DATE";

    String CLAIM_USER_NAME = "MEMBER_USERNAME";
    String CLAIM_MEMBER_NICKNAME = "MEMBER_NICKNAME";
}
