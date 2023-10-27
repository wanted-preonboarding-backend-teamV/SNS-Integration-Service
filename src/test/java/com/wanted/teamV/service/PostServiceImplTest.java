package com.wanted.teamV.service;

import com.wanted.teamV.dto.req.PostCreateReqDto;
import com.wanted.teamV.entity.Post;
import com.wanted.teamV.entity.PostHashtag;
import com.wanted.teamV.exception.CustomException;
import com.wanted.teamV.exception.ErrorCode;
import com.wanted.teamV.repository.PostHashtagRepository;
import com.wanted.teamV.repository.PostRepository;
import com.wanted.teamV.service.impl.PostServiceImpl;
import com.wanted.teamV.type.SnsType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @InjectMocks
    private PostServiceImpl postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostHashtagRepository postHashtagRepository;

    @Test
    @DisplayName("게시물 생성 - 성공")
    public void createPost_Success() {
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

        Post mockPost = Post.builder().build();
        PostHashtag mockHashtag1 = PostHashtag.builder().build();
        PostHashtag mockHashtag2 = PostHashtag.builder().build();

        when(postRepository.save(any(Post.class))).thenReturn(mockPost);
        when(postHashtagRepository.save(any(PostHashtag.class)))
                .thenReturn(mockHashtag1, mockHashtag2);

        // when
        postService.createPost(request);

        // then
        verify(postRepository, times(1)).save(any(Post.class));
        verify(postHashtagRepository, times(2)).save(any(PostHashtag.class));
    }

    @Test
    @DisplayName("게시물 생성 - 실패")
    public void createPost_Failure() {
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
        CustomException customException = assertThrows(CustomException.class, () -> postService.createPost(request));
        assertEquals(ErrorCode.INVALID_REQUEST, customException.getErrorCode());

        verifyNoInteractions(postRepository, postHashtagRepository);
    }

}