package com.wanted.teamV.service;

import com.wanted.teamV.common.ServiceTest;
import com.wanted.teamV.service.impl.AuthTokenCreator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@ServiceTest
public class AuthTokenCreatorTest {

    @Autowired
    AuthTokenCreator authTokenCreator;

    @Nested
    @DisplayName("사용자 id로 토큰을 생성한다.")
    class createAuthToken {

        @Test
        @DisplayName("성공")
        void success() {
            //given
            Long memberId = 1L;

            //when
            String accessToken = authTokenCreator.createAuthToken(memberId);

            //then
            assertThat(accessToken).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("토큰에서 페이로드를 추출한다.")
    class extractPayload {

        @Test
        @DisplayName("성공")
        void success() {
            //given
            Long memberId = 1L;
            String accessToken = authTokenCreator.createAuthToken(memberId);

            //when
            Long payload = authTokenCreator.extractPayload(accessToken);

            //then
            assertThat(payload).isEqualTo(memberId);
        }
    }
}
