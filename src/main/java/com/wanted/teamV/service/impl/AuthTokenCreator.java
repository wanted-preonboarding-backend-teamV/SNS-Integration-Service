package com.wanted.teamV.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthTokenCreator {
    private final JwtTokenProvider jwtTokenProvider;

    public String createAuthToken(Long memberId) {
        return jwtTokenProvider.createAccessToken(memberId.toString());
    }

    public Long extractPayload(String accessToken) {
        jwtTokenProvider.validateToken(accessToken);
        String payload = jwtTokenProvider.getPayload(accessToken);
        return Long.valueOf(payload);
    }
}
