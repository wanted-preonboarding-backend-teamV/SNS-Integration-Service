package com.wanted.teamV.service;

import com.wanted.teamV.exception.CustomException;
import com.wanted.teamV.service.impl.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.wanted.teamV.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class JwtTokenProviderTest {
    private static final String JWT_SECRET_KEY = "a".repeat(32); // Secret Key는 최소 32바이트 이상이어야함.
    private static final int ACCESS_TOKEN_EXPIRE_TIME = 3600;
    private final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(JWT_SECRET_KEY, ACCESS_TOKEN_EXPIRE_TIME);

    @Nested
    @DisplayName("엑세스 토큰을 생성한다.")
    class createAccessToken {

        @Test
        @DisplayName("성공")
        void success() {
            //given

            //when
            String payload = "1";
            String accessToken = jwtTokenProvider.createAccessToken(payload);

            //then
            assertThat(accessToken.split("\\.")).hasSize(3);
        }
    }

    @Nested
    @DisplayName("토큰을 검증한다.")
    class validateToken {

        @Test
        @DisplayName("성공")
        void success() {
            //given
            String payload = "1";
            String accessToken = jwtTokenProvider.createAccessToken(payload);

            //when, then
            jwtTokenProvider.validateToken(accessToken);
        }

        @Test
        @DisplayName("실패: 만료된 토큰")
        void fail() {
            //given
            JwtTokenProvider expiredJwtTokenProvider = new JwtTokenProvider(JWT_SECRET_KEY, 0);

            String payload = "1";
            String accessToken = expiredJwtTokenProvider.createAccessToken(payload);

            //when, then
            assertThatThrownBy(() -> expiredJwtTokenProvider.validateToken(accessToken))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining(EXPIRE_TOKEN.getMessage());
        }

        @Test
        @DisplayName("실패: 잘못된 토큰")
        void fail2() {
            //given

            //when, then
            String invalidToken = "token";
            assertThatThrownBy(() -> jwtTokenProvider.validateToken(invalidToken))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining(INVALID_TOKEN.getMessage());
        }
    }

    @Nested
    @DisplayName("페이로드를 추출한다")
    class getPayload {

        @Test
        @DisplayName("성공")
        void success() {
            //given
            String payload = "1";
            String accessToken = jwtTokenProvider.createAccessToken(payload);

            //when
            String actual = jwtTokenProvider.getPayload(accessToken);

            //then
            assertThat(actual).isEqualTo(payload);
        }
    }
}
