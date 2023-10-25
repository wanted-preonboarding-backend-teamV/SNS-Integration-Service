package com.wanted.teamV.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMapAdapter;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class StatisticsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getStatisticsWithDate() throws Exception {
        MultiValueMapAdapter<String, String> params = new LinkedMultiValueMap<>();
        params.add("hashtag", "맛집");
        params.add("type", "hour");

        mockMvc.perform(MockMvcRequestBuilders.get("/statistics")
                .params(params))
                .andExpect(status().isOk())
                .andDo(print());

    }
}