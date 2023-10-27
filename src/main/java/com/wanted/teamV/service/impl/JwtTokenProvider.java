package com.wanted.teamV.service.impl;

import com.wanted.teamV.exception.CustomException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static com.wanted.teamV.exception.ErrorCode.EXPIRE_TOKEN;
import static com.wanted.teamV.exception.ErrorCode.INVALID_TOKEN;

@Component
public class JwtTokenProvider {
    private final SecretKey key;
    private final long accessTokenValidityInMilliseconds;

    public JwtTokenProvider(
            @Value("${security.jwt.token.secret-key}") final String secretKey,
            @Value("${security.jwt.token.access.expire-length}") final long accessTokenValidityInMilliseconds
    ) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTokenValidityInMilliseconds = accessTokenValidityInMilliseconds;
    }

    public String createAccessToken(String payload) {
        return createToken(payload, accessTokenValidityInMilliseconds);
    }

    private String createToken(String payload, Long validityInMilliseconds) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setSubject(payload)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public void validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            claims.getBody()
                    .getExpiration()
                    .before(new Date());
        } catch (ExpiredJwtException e) {
            throw new CustomException(EXPIRE_TOKEN);
        } catch (JwtException | IllegalArgumentException e) {
            throw new CustomException(INVALID_TOKEN);
        }
    }

    public String getPayload(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
