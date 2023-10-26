package com.wanted.teamV.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanted.teamV.dto.res.PostDetailResDto;
import com.wanted.teamV.repository.MemberRepository;
import com.wanted.teamV.repository.PostRepository;
import com.wanted.teamV.service.PostService;
import com.wanted.teamV.type.SnsType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    @MockBean
    private PostRepository postRepository;

    @MockBean
    private MemberRepository memberRepository;

    @Test
    void getPostDetails() throws Exception {
        //given
        Long postId = 1L, memberId = 1L;
        PostDetailResDto response = PostDetailResDto.builder()
                .id(postId)
                .contentId("INSTA#1")
                .type(SnsType.INSTAGRAM)
                .title("test")
                .content("test1234")
                .viewCount(10)
                .likeCount(20)
                .shareCount(5)
                .postHashtags(List.of("test1", "test2"))
                .build();

        //when
        when(postService.getPostDetail(postId, memberId)).thenReturn(response);

        //then
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/{postId}/{memberId}", postId, memberId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(response.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contentId").value(response.getContentId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(response.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(response.getContent()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type").value(response.getType().name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.viewCount").value(response.getViewCount()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.likeCount").value(response.getLikeCount()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.shareCount").value(response.getShareCount()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.postHashtags").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.postHashtags[0]").value(response.getPostHashtags().get(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.postHashtags[1]").value(response.getPostHashtags().get(1)))
                .andDo(print());

    }
}
