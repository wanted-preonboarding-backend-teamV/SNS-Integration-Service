package com.wanted.teamV.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.wanted.teamV.dto.req.MemberApproveReqDto;
import com.wanted.teamV.dto.req.MemberJoinReqDto;
import com.wanted.teamV.dto.req.MemberLoginReqDto;
import com.wanted.teamV.dto.req.PostCreateReqDto;
import com.wanted.teamV.dto.res.PostDetailResDto;
import com.wanted.teamV.exception.ErrorCode;
import com.wanted.teamV.type.SnsType;
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

    private String token;

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

    @BeforeEach
    @Test
    @DisplayName("게시물 생성 - 성공")
    public void createPost_Success() throws Exception {
        // given
        String contentId = "post1";
        String title = "성수동 맛집 투어";
        String content = "오늘은 성수동 맛집 투어를 다녀왔습니다. 총 3군데를 다녀왔는데 여기는 어쩌구 저기는 저쩌구 했습니다.";
        String type = "instagram";
        int viewCount = 100;
        int likeCount = 40;
        int shareCount = 10;
        List<String> hashtags = Arrays.asList("맛집", "성수동");

        PostCreateReqDto request1 = PostCreateReqDto.builder()
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
                        .content(objectMapper.writeValueAsString(request1))
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        // given
        String contentId2 = "post2";
        String title2 = "맛집 투어";
        String content2 = "오늘은 성수동 맛집 투어를 다녀왔습니다. 총 3군데를 다녀왔는데 여기는 어쩌구 저기는 저쩌구 했습니다.";
        String type2 = "facebook";
        int viewCount2 = 50;
        int likeCount2 = 35;
        int shareCount2 = 20;
        List<String> hashtags2 = Arrays.asList("맛집", "성수동");

        PostCreateReqDto request2 = PostCreateReqDto.builder()
                .contentId(contentId2)
                .title(title2)
                .content(content2)
                .type(type2)
                .viewCount(viewCount2)
                .likeCount(likeCount2)
                .shareCount(shareCount2)
                .hashtags(hashtags2)
                .build();

        // when & then
        mockMvc.perform(post("/posts")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2))
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
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.INVALID_REQUEST.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_REQUEST.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("게시물 조회 해시태그 및 정렬 - 성공")
    public void getPosts_Hashtag_Order() throws Exception {
        // given
        MultiValueMapAdapter<String, String> params = new LinkedMultiValueMap<>();
        params.add("hashtag", "맛집");

        // 해시태그 검색
        mockMvc.perform(MockMvcRequestBuilders.get("/posts")
                        .params(params)
                        .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andDo(print());

        // 정렬
        params.add("orderBy", "share_count_desc");

        mockMvc.perform(MockMvcRequestBuilders.get("/posts")
                        .params(params)
                        .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].contentId").value("post2"))
                .andDo(print());
    }

    @Test
    @DisplayName("게시물 조회 해시태그 입력 없을 때 - 성공")
    public void getPosts_no_hashtag() throws Exception {
        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/posts")
                        .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].contentId").value("post2"))
                .andExpect(jsonPath("$.content[1].contentId").value("post1"))
                .andDo(print());
    }

    @Test
    @DisplayName("게시물 조회 타입 검색 - 성공")
    public void getPosts_filterByType() throws Exception {
        // given
        MultiValueMapAdapter<String, String> params = new LinkedMultiValueMap<>();
        params.add("hashtag", "맛집");
        params.add("type", "instagram");

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/posts")
                        .params(params)
                        .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].contentId").value("post1"))
                .andDo(print());
    }

    @Test
    @DisplayName("게시물 조회 제목 검색 - 성공")
    public void getPosts_searchByTitle() throws Exception {
        // given
        MultiValueMapAdapter<String, String> params = new LinkedMultiValueMap<>();
        params.add("hashtag", "맛집");
        params.add("searchBy", "title");
        params.add("search", "성수동");

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/posts")
                        .params(params)
                        .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].contentId").value("post1"))
                .andDo(print());
    }

    @Test
    @DisplayName("게시물 조회 페이징 처리 - 성공")
    public void getPosts_Pageable() throws Exception {
        // given
        MultiValueMapAdapter<String, String> params = new LinkedMultiValueMap<>();
        params.add("hashtag", "맛집");
        params.add("pageCount", "1");
        params.add("page", "1");

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/posts")
                        .params(params)
                        .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].contentId").value("post1"))
                .andDo(print());
    }

    @Test
    @DisplayName("게시물 조회 없는 페이지 요청")
    public void getPosts_Pageable_Failure() throws Exception {
        // given
        MultiValueMapAdapter<String, String> params = new LinkedMultiValueMap<>();
        params.add("hashtag", "맛집");
        params.add("pageCount", "1");
        params.add("page", "3");

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/posts")
                        .params(params)
                        .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.INVALID_PAGE_REQUEST.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_PAGE_REQUEST.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("게시물 조회 - 관련 게시물 없음")
    public void getPosts_NotRelatedPosts() throws Exception {
        //given
        MultiValueMapAdapter<String, String> params = new LinkedMultiValueMap<>();
        params.add("hashtag", "음식");

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/posts")
                        .params(params)
                        .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("게시물 조회 잘못된 SNS 타입 요청")
    public void getPosts_InvalidType() throws Exception {
        // given
        MultiValueMapAdapter<String, String> params = new LinkedMultiValueMap<>();
        params.add("hashtag", "맛집");
        params.add("type", "twitter");

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/posts")
                        .params(params)
                        .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.INVALID_SNS_TYPE.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_SNS_TYPE.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("게시물 상세 정보 조회 - 성공")
    public void getPostDetails() throws Exception {
        //given
        Long postId = 1L;

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/post/{postId}", postId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("게시물 상세 정보 조회 - 실패(게시물 없음)")
    public void getPostDetails_No_Post() throws Exception {
        // given
        Long postId = 3L;

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/post/{postId}", postId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.POST_NOT_FOUND.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.POST_NOT_FOUND.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("게시물 좋아요 - 성공")
    public void increaseLikeCount() throws Exception {
        //given
        Long postId = 1L;

        //when & then
        mockMvc.perform(post("/posts/likes/{postId}", postId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("게시물 좋아요 - 실패(게시물 없음)")
    public void increaseLikeCount_No_Post() throws Exception {
        //given
        Long postId = 3L;

        //when & then
        mockMvc.perform(post("/posts/likes/{postId}", postId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.POST_NOT_FOUND.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.POST_NOT_FOUND.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("게시물 공유 - 성공")
    public void increaseShareCount() throws Exception {
        //given
        Long postId = 1L;

        //when & then
        mockMvc.perform(post("/posts/shares/{postId}", postId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("게시물 공유 - 실패(게시물 없음)")
    public void increaseShareCount_No_Post() throws Exception {
        //given
        Long postId = 3L;

        //when & then
        mockMvc.perform(post("/posts/shares/{postId}", postId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.POST_NOT_FOUND.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.POST_NOT_FOUND.getMessage()))
                .andDo(print());
    }
}
