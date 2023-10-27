package com.wanted.teamV.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.wanted.teamV.dto.req.MemberApproveReqDto;
import com.wanted.teamV.dto.req.MemberJoinReqDto;
import com.wanted.teamV.dto.req.MemberLoginReqDto;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMapAdapter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class StatisticsControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private String accessToken;

    @BeforeEach
    void setUp() throws Exception {
        // 회원가입
        MemberJoinReqDto joinReqDto = new MemberJoinReqDto("qwertyasdf12345", "abc@gmail.com", "qwerty1234!");
        MvcResult result = mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinReqDto))
                )
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        String code = JsonPath.parse(response).read("$.code");

        // 가입승인
        MemberApproveReqDto approveReqDto = new MemberApproveReqDto("qwertyasdf12345", "qwerty1234!", code);
        mockMvc.perform(post("/members/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(approveReqDto))
                )
                .andExpect(status().isNoContent());

        // 로그인
        MemberLoginReqDto loginReqDto = new MemberLoginReqDto("qwertyasdf12345", "qwerty1234!");
        result = mockMvc.perform(post("/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReqDto))
                )
                .andExpect(status().isOk())
                .andReturn();

        response = result.getResponse().getContentAsString();
        accessToken = JsonPath.parse(response).read("$.accessToken");
    }

    @Test
    @DisplayName("통계 조회 API의 응답 형식이 잘 맞는지 확인한다. (일자/시간 및 정렬")
    public void response_format_test() throws Exception {
        MultiValueMapAdapter<String, String> params = new LinkedMultiValueMap<>();
        params.add("hashtag", "맛집");

        // 일자, 내림차순
        params.add("start", "2023-10-10");
        params.add("end", "2023-10-13");

        mockMvc.perform(MockMvcRequestBuilders.get("/statistics")
                        .params(params)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].time").value("2023-10-13"))
                .andDo(print());

        // 일자, 오름차순
        params.add("sort", "asc");

        mockMvc.perform(MockMvcRequestBuilders.get("/statistics")
                        .params(params)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].time").value("2023-10-10"))
                .andDo(print());

        // 시간, 오름차순
        params.add("type", "hour");

        mockMvc.perform(MockMvcRequestBuilders.get("/statistics")
                        .params(params)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4 * 24))
                .andExpect(jsonPath("$[0].time").value("2023-10-10T00:00:00"))
                .andDo(print());

        // 시간, 내림차순
        params.remove("sort");
        mockMvc.perform(MockMvcRequestBuilders.get("/statistics")
                        .params(params)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4 * 24))
                .andExpect(jsonPath("$[0].time").value("2023-10-13T00:00:00"))
                .andDo(print());
    }

    @Test
    @DisplayName("조회 가능 기간을 초과하여 조회에 실패한다.")
    public void exceed_max_period() throws Exception {
        // type=date인 경우 최대 31일
        MultiValueMapAdapter<String, String> params = new LinkedMultiValueMap<>();
        params.add("hashtag", "맛집");
        params.add("start", "2022-10-01");
        params.add("end", "2022-11-01");

        mockMvc.perform(MockMvcRequestBuilders.get("/statistics")
                        .params(params)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.INVALID_STATISTICS_DATE.name()))
                .andDo(print());

        // type=hour인 경우 최대 7일
        params.add("type", "hour");
        params.set("end", "2022-10-08");
        mockMvc.perform(MockMvcRequestBuilders.get("/statistics")
                        .params(params)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.INVALID_STATISTICS_DATE.name()))
                .andDo(print());
    }

    @Test
    @DisplayName("hashtag 값이 없는 경우 토큰에서 사용자 정보를 구하여 계정을 대신 사용한다.")
    public void no_hashtag_but_has_token() throws Exception {
        // 통계 조회
        MultiValueMapAdapter<String, String> params = new LinkedMultiValueMap<>();
        params.add("start", "2022-10-01");
        params.add("end", "2022-10-10");

        mockMvc.perform(MockMvcRequestBuilders.get("/statistics")
                        .params(params)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andDo(print());
    }
}