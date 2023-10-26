package com.wanted.teamV.dto.req;

import jakarta.validation.constraints.Email;

public record MemberJoinReqDto(
        String account,
        @Email
        String email,
        String password
) {
}
