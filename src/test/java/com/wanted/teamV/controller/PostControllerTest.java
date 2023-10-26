package com.wanted.teamV.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanted.teamV.dto.req.PostCreateReqDto;
import com.wanted.teamV.entity.Post;
import com.wanted.teamV.entity.PostHashtag;
import com.wanted.teamV.exception.ErrorCode;
import com.wanted.teamV.repository.PostHashtagRepository;
import com.wanted.teamV.repository.PostRepository;
import com.wanted.teamV.type.SnsType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostHashtagRepository postHashtagRepository;

    @Test
    @DisplayName("게시물 생성 - 성공")
    public void createPost_Success() throws Exception {
        // given
        String contentId = "12345";
        String title = "저녁";
        String content = "저녁먹음";
        SnsType type = SnsType.INSTAGRAM;
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
        MvcResult result = mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        Post createdPost = postRepository.findById(1L).get();
        List<PostHashtag> postHashtags = postHashtagRepository.findByPostId(1L);
        List<String> postedHashtags = new ArrayList<>();
        for (PostHashtag postHashtag : postHashtags) {
            postedHashtags.add(postHashtag.getHashtag());
        }

        Assertions.assertEquals(contentId, createdPost.getContentId());
        Assertions.assertEquals(title, createdPost.getTitle());
        Assertions.assertEquals(content, createdPost.getContent());
        Assertions.assertEquals(type, createdPost.getType());
        Assertions.assertEquals(viewCount, createdPost.getViewCount());
        Assertions.assertEquals(likeCount, createdPost.getLikeCount());
        Assertions.assertEquals(shareCount, createdPost.getShareCount());
        Assertions.assertEquals(hashtags, postedHashtags);
    }

    @Test
    @DisplayName("게시물 생성 - 실패")
    public void createPost_Failure() throws Exception {
        // given
        String invalidContentId = "";
        String title = "저녁";
        String content = "저녁먹음";
        SnsType type = SnsType.INSTAGRAM;
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_REQUEST.getMessage()))
                .andDo(print());
    }

}