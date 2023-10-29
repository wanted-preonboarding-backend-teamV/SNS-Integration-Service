package com.wanted.teamV.service;

import com.wanted.teamV.ServiceTest;
import com.wanted.teamV.dto.req.MemberJoinReqDto;
import com.wanted.teamV.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@ServiceTest
public class MemberServiceImplTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

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
            boolean actual = memberRepository.existsByAccount("namse");
            assertThat(actual).isTrue();
        }
    }
}
