package com.wanted.teamV.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.wanted.teamV.dto.req.MemberApproveReqDto;
import com.wanted.teamV.dto.req.MemberJoinReqDto;
import com.wanted.teamV.dto.req.MemberLoginReqDto;
import com.wanted.teamV.dto.req.PostCreateReqDto;
import com.wanted.teamV.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    String token;

    @BeforeEach
    public void commonSetup() throws Exception {
        // 회원가입
        MemberJoinReqDto joinReqDto = new MemberJoinReqDto("mockUser", "mockUser@gmail.com", "mockUser1234!");
        MvcResult result = mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinReqDto))
                )
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        String code = JsonPath.parse(response).read("$.code");

        // 가입승인
        MemberApproveReqDto approveReqDto = new MemberApproveReqDto("mockUser", "mockUser1234!", code);
        mockMvc.perform(post("/members/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(approveReqDto))
                )
                .andExpect(status().isNoContent());

        // 로그인
        MemberLoginReqDto loginReqDto = new MemberLoginReqDto("mockUser", "mockUser1234!");
        result = mockMvc.perform(post("/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReqDto))
                )
                .andExpect(status().isOk())
                .andReturn();

        response = result.getResponse().getContentAsString();
        token = JsonPath.parse(response).read("$.accessToken");
    }

    @Test
    @DisplayName("게시물 생성 - 성공")
    public void createPost_Success() throws Exception {
        // given
        String contentId = "12345";
        String title = "저녁";
        String content = "저녁먹음";
        String type = "instagram";
        int viewCount = 0;
        int likeCount = 0;
        int shareCount = 0;
        List<String> hashtags = Arrays.asList("음식", "dinner");

        PostCreateReqDto request = PostCreateReqDto.builder()
                .contentId(contentId)
                .title(title)
                .content(content)
                .type(type)
                .viewCount(viewCount)
                .likeCount(likeCount)
                .shareCount(shareCount)
                .hashtags(hashtags)
                .build();

        // when & then
        mockMvc.perform(post("/posts")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
    }

    @Test
    @DisplayName("게시물 생성 - 실패")
    public void createPost_Failure() throws Exception {
        // given
        String invalidContentId = "";
        String title = "저녁";
        String content = "저녁먹음";
        String type = "instagram";
        int viewCount = 0;
        int likeCount = 0;
        int shareCount = 0;
        List<String> hashtags = Arrays.asList("음식", "dinner");

        PostCreateReqDto request = PostCreateReqDto.builder()
                .contentId(invalidContentId)
                .title(title)
                .content(content)
                .type(type)
                .viewCount(viewCount)
                .likeCount(likeCount)
                .shareCount(shareCount)
                .hashtags(hashtags)
                .build();

        // when & then
        mockMvc.perform(post("/posts")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_REQUEST.getMessage()))
                .andDo(print());
    }

}