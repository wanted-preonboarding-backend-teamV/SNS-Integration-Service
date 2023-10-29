package com.wanted.teamV.service;

import com.wanted.teamV.dto.req.MemberJoinReqDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class MemberServiceImplTest {

    @Autowired
    MemberService memberService;

    @Nested
    @DisplayName("회원가입을 한다.")
    class join {

        @Test
        @DisplayName("성공")
        void success() {
            //given
            MemberJoinReqDto memberJoinReqDto = new MemberJoinReqDto("namse", "test@gmail.com", "test@2123#@");

            //when
            memberService.join(memberJoinReqDto);

            //then
        }

        @Test
        @DisplayName("실패: 테스트")
        void fail() {
            //given
            MemberJoinReqDto memberJoinReqDto = new MemberJoinReqDto("namse", "test@gmail.com", "test@2123#@");
            memberService.join(memberJoinReqDto);

            //when
            MemberJoinReqDto memberJoinReqDto2 = new MemberJoinReqDto("namse", "test@gmail.com", "test@2123#@");
            memberService.join(memberJoinReqDto2);

            //then
        }
    }
}
