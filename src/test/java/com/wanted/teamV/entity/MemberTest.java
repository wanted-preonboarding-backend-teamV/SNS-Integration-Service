package com.wanted.teamV.entity;

import com.wanted.teamV.exception.CustomException;
import com.wanted.teamV.type.MemberStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.wanted.teamV.exception.ErrorCode.INVALID_AUTHENTICATION_CODE;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MemberTest {

    @Nested
    @DisplayName("인증코드를 검사한다.")
    class verifyCode {

        @Test
        @DisplayName("성공")
        void success() {
            //given
            Member member = Member.builder()
                    .account("namse")
                    .password("password")
                    .email("email")
                    .code("184721")
                    .status(MemberStatus.WAITING)
                    .build();

            //when, then
            member.verifyCode("184721");
        }

        @Test
        @DisplayName("실패: 잘못된 인증코드")
        void fail() {
            //given
            Member member = Member.builder()
                    .account("namse")
                    .password("password")
                    .email("email")
                    .code("184721")
                    .status(MemberStatus.WAITING)
                    .build();

            //when, then
            String invalidCode = "123456";
            assertThatThrownBy(() -> member.verifyCode(invalidCode))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining(INVALID_AUTHENTICATION_CODE.getMessage());
        }
    }
}
