package com.wanted.teamV.controller;

import com.wanted.teamV.common.ControllerTest;
import com.wanted.teamV.dto.req.MemberApproveReqDto;
import com.wanted.teamV.dto.req.MemberJoinReqDto;
import com.wanted.teamV.dto.req.MemberLoginReqDto;
import com.wanted.teamV.dto.res.MemberCodeResDto;
import com.wanted.teamV.dto.res.MemberTokenResDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MemberControllerTest extends ControllerTest {

    @Nested
    @DisplayName("회원가입을 한다.")
    class join {

        @Test
        @DisplayName("성공")
        void success() throws Exception {
            MemberJoinReqDto memberJoinReqDto = new MemberJoinReqDto("namse", "test@gmail.com", "test@2123#@");
            MemberCodeResDto response = new MemberCodeResDto("123456");

            given(memberService.join(any()))
                    .willReturn(response);

            mockMvc.perform(post("/members")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(memberJoinReqDto))
                    )
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("가입승인을 요청한다.")
    class approve {

        @Test
        @DisplayName("성공")
        void success() throws Exception {
            MemberApproveReqDto memberApproveReqDto = new MemberApproveReqDto("namse", "test@2123#@", "123456");

            willDoNothing()
                    .given(memberService)
                    .approve(any());

            mockMvc.perform(post("/members/approve")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(memberApproveReqDto))
                    )
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }
    }

    @Nested
    @DisplayName("로그인을 한다.")
    class login {

        @Test
        @DisplayName("성공")
        void success() throws Exception {
            MemberLoginReqDto memberJoinReqDto = new MemberLoginReqDto("namse","test@2123#@");
            MemberTokenResDto response = new MemberTokenResDto("12414.214r3dsa.erafdsf");

            given(memberService.login(any()))
                    .willReturn(response);

            mockMvc.perform(post("/members/login")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(memberJoinReqDto))
                    )
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }
}
