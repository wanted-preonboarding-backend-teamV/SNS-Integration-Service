package com.wanted.teamV.service;

import com.wanted.teamV.common.ServiceTest;
import com.wanted.teamV.dto.req.MemberApproveReqDto;
import com.wanted.teamV.dto.req.MemberJoinReqDto;
import com.wanted.teamV.dto.req.MemberLoginReqDto;
import com.wanted.teamV.dto.res.MemberCodeResDto;
import com.wanted.teamV.dto.res.MemberTokenResDto;
import com.wanted.teamV.entity.Member;
import com.wanted.teamV.exception.CustomException;
import com.wanted.teamV.repository.MemberRepository;
import com.wanted.teamV.type.MemberStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.wanted.teamV.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ServiceTest
public class MemberServiceImplTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

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

        @Test
        @DisplayName("실패: 중복되는 계정")
        void fail() {
            //given
            Member member = Member.builder()
                    .account("namse")
                    .password("password")
                    .email("email")
                    .code("184721")
                    .status(MemberStatus.WAITING)
                    .build();
            memberRepository.save(member);

            //when, then
            MemberJoinReqDto memberJoinReqDto = new MemberJoinReqDto("namse", "test@gmail.com", "test@2123#@");

            assertThatThrownBy(() -> memberService.join(memberJoinReqDto))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining(DUPLICATE_ACCOUNT.getMessage());
        }
    }

    @Nested
    @DisplayName("가입승인을 한다.")
    class approve {

        @Test
        @DisplayName("성공")
        void success() {
            //given
            MemberJoinReqDto memberJoinReqDto = new MemberJoinReqDto("namse", "test@gmail.com", "test@2123#@");
            MemberCodeResDto memberCodeResDto = memberService.join(memberJoinReqDto);

            //when
            MemberApproveReqDto memberApproveReqDto = new MemberApproveReqDto("namse", "test@2123#@", memberCodeResDto.code());
            memberService.approve(memberApproveReqDto);

            //then
            Member member = memberRepository.getByAccount("namse");
            assertThat(member.getStatus()).isEqualTo(MemberStatus.APPROVE);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 계정")
        void fail() {
            //given

            //when, then
            MemberApproveReqDto memberApproveReqDto = new MemberApproveReqDto("namse", "test@2123#@", "182472");

            assertThatThrownBy(() -> memberService.approve(memberApproveReqDto))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining(NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("실패: 잘못된 비밀번호")
        void fail2() {
            //given
            Member member = Member.builder()
                    .account("namse")
                    .password(passwordEncoder.encode("test@2123#@"))
                    .email("email")
                    .code("184721")
                    .status(MemberStatus.APPROVE)
                    .build();
            memberRepository.save(member);

            //when, then
            String invalidPassword = "password";
            MemberApproveReqDto memberApproveReqDto = new MemberApproveReqDto("namse", invalidPassword, "184721");

            assertThatThrownBy(() -> memberService.approve(memberApproveReqDto))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining(INVALID_PASSWORD.getMessage());
        }

        @Test
        @DisplayName("실패: 잘못된 인증코드")
        void fail3() {
            //given
            Member member = Member.builder()
                    .account("namse")
                    .password(passwordEncoder.encode("test@2123#@"))
                    .email("email")
                    .code("184721")
                    .status(MemberStatus.APPROVE)
                    .build();
            memberRepository.save(member);

            //when, then
            String invalidCode = "123456";
            MemberApproveReqDto memberApproveReqDto = new MemberApproveReqDto("namse", "test@2123#@", invalidCode);

            assertThatThrownBy(() -> memberService.approve(memberApproveReqDto))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining(INVALID_AUTHENTICATION_CODE.getMessage());
        }
    }

    @Nested
    @DisplayName("로그인을 한다.")
    class login {

        @Test
        @DisplayName("성공")
        void success() {
            //given
            Member member = Member.builder()
                    .account("namse")
                    .password(passwordEncoder.encode("test@2123#@"))
                    .email("email")
                    .code("184721")
                    .status(MemberStatus.APPROVE)
                    .build();
            memberRepository.save(member);

            //when
            MemberLoginReqDto memberLoginReqDto = new MemberLoginReqDto("namse", "test@2123#@");
            MemberTokenResDto memberTokenResDto = memberService.login(memberLoginReqDto);

            //then
            assertThat(memberTokenResDto.accessToken()).isNotEmpty();
        }

        @Test
        @DisplayName("실패: 가입승인이 안된 사용자")
        void fail() {
            //given
            Member member = Member.builder()
                    .account("namse")
                    .password(passwordEncoder.encode("test@2123#@"))
                    .email("email")
                    .code("184721")
                    .status(MemberStatus.WAITING)
                    .build();
            memberRepository.save(member);

            //when, then
            MemberLoginReqDto memberLoginReqDto = new MemberLoginReqDto("namse", "test@2123#@");

            assertThatThrownBy(() -> memberService.login(memberLoginReqDto))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining(NOT_APPROVED.getMessage());
        }
    }
}
