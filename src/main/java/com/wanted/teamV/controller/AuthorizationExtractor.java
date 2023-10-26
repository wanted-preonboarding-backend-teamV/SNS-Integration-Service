package com.wanted.teamV.controller;

import com.wanted.teamV.exception.CustomException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;

import static com.wanted.teamV.exception.ErrorCode.EMPTY_AUTHORIZATION_HEADER;
import static com.wanted.teamV.exception.ErrorCode.INVALID_TOKEN;

public class AuthorizationExtractor {
    private static final String BEARER_TYPE = "Bearer ";

    public static String extract(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new CustomException(EMPTY_AUTHORIZATION_HEADER);
        }

        validateAuthorizationFormat(authorizationHeader);
        return authorizationHeader.substring(BEARER_TYPE.length()).trim();
    }

    private static void validateAuthorizationFormat(String authorizationHeader) {
        if (!authorizationHeader.toLowerCase().startsWith(BEARER_TYPE.toLowerCase())) {
            throw new CustomException(INVALID_TOKEN);
        }
    }
}
