package com.project.uandmeet.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.uandmeet.exception.CustomException;
import com.project.uandmeet.exception.ErrorCode;
import com.project.uandmeet.security.UserDetailsImpl;
import io.jsonwebtoken.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

import static com.project.uandmeet.security.jwt.JwtProperties.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    @Value("${jwt.secretKey}")
    private String secretKey;
    public final HttpServletResponse response;

    private final UserDetailsService userDetailsService;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // 토큰 생성
    public String createToken(String userPk, Long userId) {
        Claims claims = Jwts.claims().setSubject(userPk);
        Date now = new Date();
        String token= Jwts.builder()
                .setClaims(claims)//정보저장
                .claim("userId", userId)
                .setIssuedAt(now)//토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + ACCESS_EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, secretKey)//사용할 암호화 알고리즘
                //signature에 들어갈 secret값 세팅
                .compact();


        response.addHeader(HEADER_ACCESS, TOKEN_PREFIX + token);
        return token;
    }

    public String createRefreshToken(String userPk) {
        Claims claims = Jwts.claims().setSubject(userPk);
        Date now = new Date();
        String refreshToken= Jwts.builder()
                .setClaims(claims)//정보저장
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + REFRESH_EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

//        response.addHeader("RefreshToken","Bearer " + refreshToken);
        return refreshToken;
    }

    public String decodeUsername(String token) {
        DecodedJWT decodedJWT = isValidToken(token)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_AUTH_TOKEN));

        Date expiredDate = decodedJWT
                .getClaim(CLAIM_EXPIRED_DATE)
                .asDate();

        Date now = new Date();
        if (expiredDate.before(now)) {
            throw new CustomException(ErrorCode.INVALID_AUTH_TOKEN);
        }

        String username = decodedJWT
                .getClaim(CLAIM_USER_NAME)
                .asString();

        return username;
    }

    public String decodeNickname(String token) {
        DecodedJWT decodedJWT = isValidToken(token)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_AUTH_TOKEN));

        Date expiredDate = decodedJWT
                .getClaim(CLAIM_EXPIRED_DATE)
                .asDate();

        Date now = new Date();
        if (expiredDate.before(now)) {
            throw new CustomException(ErrorCode.INVALID_AUTH_TOKEN);
        }

        String nickname = decodedJWT
                .getClaim(CLAIM_MEMBER_NICKNAME)
                .asString();

        return nickname;
    }

    private Optional<DecodedJWT> isValidToken(String token) {
        DecodedJWT jwt = null;

        try {
            Algorithm algorithm = Algorithm.HMAC512(String.valueOf(secretKey));
            JWTVerifier verifier = JWT
                    .require(algorithm)
                    .build();

            jwt = verifier.verify(token);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return Optional.ofNullable(jwt);
    }

    // 토큰에서 회원 정보 추출
    public String getUserPk(String jwtToken) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(setTokenName(jwtToken)).getBody().getSubject();
    }

    // JWT 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String jwtToken) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPk(setTokenName(jwtToken)));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    //Request의 Header에서 token 값을 가져옴
    //"X-AUTH-TOKEN":"TOKEN 값"
    public String resolveToken(HttpServletRequest request) {
//        return request.getHeader("Authorization");
        return request.getHeader("AccessToken");
    }
    public String resolveRefreshToken(HttpServletRequest request) {
        return request.getHeader("RefreshToken");
    }

    // 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(setTokenName(jwtToken));
            return !claims.getBody().getExpiration().before(new Date());
        } catch (SecurityException e) {
            log.info("Invalid JWT signature.");
            throw new JwtException("잘못된 JWT 시그니처");
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT token.");
            throw new JwtException("유효하지 않은 JWT 토큰");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
            throw new JwtException("토큰 기한 만료");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
            throw new JwtException("JWT token compact of handler are invalid.");
        }
        return false;
    }



    // 만료 기간 확인
    public Date ExpireTime(String token){
        Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(setTokenName(token));
        return claims.getBody().getExpiration();
    }

    // Bearer 삭제
    public String setTokenName(String bearerToken){
        return bearerToken.replace(TOKEN_PREFIX, "");
    }

    // 만료 accessToken 정보 추출
    public String getExpiredAccessTokenPk(String token) throws JsonProcessingException {
        String[] splitJwt = token.split("\\.");
        Base64.Decoder decoder = Base64.getDecoder();
        String payload = new String(decoder.decode(splitJwt[1].getBytes()));
        HashMap<String, String> payloadMap = new ObjectMapper().readValue(payload, HashMap.class);
        return payloadMap.get("sub");
    }


}

