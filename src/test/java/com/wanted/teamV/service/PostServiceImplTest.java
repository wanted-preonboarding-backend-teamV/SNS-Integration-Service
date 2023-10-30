package com.wanted.teamV.service;

import com.wanted.teamV.dto.req.PostCreateReqDto;
import com.wanted.teamV.dto.res.ListResDto;
import com.wanted.teamV.dto.res.PostResDto;
import com.wanted.teamV.entity.Post;
import com.wanted.teamV.entity.PostHashtag;
import com.wanted.teamV.exception.CustomException;
import com.wanted.teamV.exception.ErrorCode;
import com.wanted.teamV.repository.PostHashtagRepository;
import com.wanted.teamV.repository.PostRepository;
import com.wanted.teamV.service.impl.PostServiceImpl;
import com.wanted.teamV.type.OrderByType;
import com.wanted.teamV.type.SearchByType;
import com.wanted.teamV.type.SnsType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

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

    @Test
    @DisplayName("게시물 조회 - 성공")
    public void getPosts_Success() {
        // given
        String hashtag = "맛집";
        SnsType type = SnsType.INSTAGRAM;
        OrderByType orderBy = OrderByType.LIKE_COUNT_DESC;
        SearchByType searchBy = SearchByType.TITLE;
        String search = "성수동";
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        Post post1 = Post.builder()
                .contentId("post1")
                .title("성수동 맛집 투어")
                .content("오늘은 성수동 맛집 투어를 다녀왔습니다. 총 3군데를 다녀왔는데 여기는 어쩌구 저기는 저쩌구 했습니다.")
                .type(SnsType.INSTAGRAM)
                .viewCount(100)
                .likeCount(40)
                .shareCount(10)
                .build();

        Post post2 = Post.builder()
                .contentId("post2")
                .title("맛집 투어")
                .content("오늘은 성수동 맛집 투어를 다녀왔습니다. 총 3군데를 다녀왔는데 여기는 어쩌구 저기는 저쩌구 했습니다.")
                .type(SnsType.INSTAGRAM)
                .viewCount(50)
                .likeCount(10)
                .shareCount(5)
                .build();

        List<Long> postIds = Arrays.asList(post1.getId(), post2.getId());
        when(postHashtagRepository.findPostIdsByHashtag(hashtag)).thenReturn(postIds);

        List<Post> filteredPosts = Arrays.asList(post1);
        when(postRepository.filterPosts(postIds, type, orderBy, searchBy, search)).thenReturn(filteredPosts);

        // when
        ListResDto<PostResDto> response = postService.getPosts(hashtag, type, orderBy, searchBy, search, pageable);

        // then
        assertEquals(1, response.getNumberOfElements());
        assertEquals(post1.getContentId(), response.getContent().get(0).getContentId());
        assertEquals(post1.getType(), response.getContent().get(0).getType());
        assertEquals(post1.getTitle(), response.getContent().get(0).getTitle());
        assertEquals(post1.getContent().substring(0, 20), response.getContent().get(0).getContent());
        assertEquals(post1.getViewCount(), response.getContent().get(0).getViewCount());
        assertEquals(post1.getLikeCount(), response.getContent().get(0).getLikeCount());
        assertEquals(post1.getShareCount(), response.getContent().get(0).getShareCount());
    }

    @Test
    @DisplayName("게시물 조회 - 관련 게시물 없음")
    public void getPosts_NoRelatedPosts() {
        // given
        String hashtag = "한국";
        SnsType type = SnsType.INSTAGRAM;
        OrderByType orderBy = OrderByType.LIKE_COUNT_DESC;
        SearchByType searchBy = SearchByType.TITLE;
        String search = "명동";
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        postHashtagRepository.findPostIdsByHashtag(hashtag);

        // when & then
        CustomException customException = assertThrows(CustomException.class,
                () -> postService.getPosts(hashtag, type, orderBy, searchBy, search, pageable));
        assertEquals(ErrorCode.NO_RELATED_POSTS_FOUND, customException.getErrorCode());
    }

    @Test
    @DisplayName("게시물 조회 - 관련 게시물 없음")
    public void getPosts_NoRelatedPosts2() {
        // given
        String hashtag = "맛집";
        SnsType type = SnsType.INSTAGRAM;
        OrderByType orderBy = OrderByType.LIKE_COUNT_DESC;
        SearchByType searchBy = SearchByType.TITLE;
        String search = "명동";
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        Post post1 = Post.builder()
                .contentId("post1")
                .title("성수동 맛집 투어")
                .content("오늘은 성수동 맛집 투어를 다녀왔습니다. 총 3군데를 다녀왔는데 여기는 어쩌구 저기는 저쩌구 했습니다.")
                .type(SnsType.INSTAGRAM)
                .viewCount(100)
                .likeCount(40)
                .shareCount(10)
                .build();

        List<Long> postIds = Arrays.asList(post1.getId());
        when(postHashtagRepository.findPostIdsByHashtag(hashtag)).thenReturn(postIds);

        postRepository.filterPosts(postIds, type, orderBy, searchBy, search);

        // when & then
        CustomException customException = assertThrows(CustomException.class,
                () -> postService.getPosts(hashtag, type, orderBy, searchBy, search, pageable));
        assertEquals(ErrorCode.NO_RELATED_POSTS_FOUND, customException.getErrorCode());
    }

}