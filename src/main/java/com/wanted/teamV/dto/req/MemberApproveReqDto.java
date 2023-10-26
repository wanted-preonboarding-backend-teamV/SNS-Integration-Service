package com.wanted.teamV.dto.req;

public record MemberApproveReqDto(
        String account,
        String password,
        String code
) {
}
