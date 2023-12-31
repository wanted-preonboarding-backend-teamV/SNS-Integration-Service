package com.wanted.teamV.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    NOT_FOUND(HttpStatus.BAD_REQUEST, "요청사항을 찾지 못했습니다."),
    POST_NOT_FOUND(HttpStatus.BAD_REQUEST, "게시물을 찾지 못했습니다."),
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "사용자를 찾지 못했습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INVALID_STATISTICS_DATE(HttpStatus.BAD_REQUEST, "잘못된 조회 기간입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에 문제가 발생했습니다."),
    INVALID_PAGE_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 페이지 요청입니다."),
    NO_RELATED_POSTS_FOUND(HttpStatus.NOT_FOUND, "관련 게시물이 없습니다."),
    INVALID_SNS_TYPE(HttpStatus.BAD_REQUEST, "지원되지 않는 sns 타입 입니다."),
    INVALID_AUTHENTICATION_CODE(HttpStatus.BAD_REQUEST, "인증코드가 올바르지 않습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "잘못된 비밀번호입니다."),
    DUPLICATE_ACCOUNT(HttpStatus.BAD_REQUEST, "중복된 계정입니다."),
    EXPIRE_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 토큰입니다."),
    EMPTY_AUTHORIZATION_HEADER(HttpStatus.BAD_REQUEST, "인증헤더가 비어있습니다."),
    NOT_APPROVED(HttpStatus.UNAUTHORIZED, "가입이 아직 승인되지 않았습니다.")
    ;

    private final HttpStatus status;
    private final String message;

    ErrorCode(final HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
