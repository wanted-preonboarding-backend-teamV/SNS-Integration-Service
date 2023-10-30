package com.wanted.teamV.service;

import com.wanted.teamV.dto.req.PostCreateReqDto;
import com.wanted.teamV.dto.res.PostDetailResDto;
import com.wanted.teamV.dto.res.PostResDto;
import com.wanted.teamV.entity.Member;
import com.wanted.teamV.entity.Post;
import com.wanted.teamV.entity.PostHashtag;
import com.wanted.teamV.entity.PostHistory;
import com.wanted.teamV.exception.CustomException;
import com.wanted.teamV.exception.ErrorCode;
import com.wanted.teamV.repository.MemberRepository;
import com.wanted.teamV.repository.PostHashtagRepository;
import com.wanted.teamV.repository.PostHistoryRepository;
import com.wanted.teamV.repository.PostRepository;
import com.wanted.teamV.service.impl.PostServiceImpl;
import com.wanted.teamV.type.HistoryType;
import com.wanted.teamV.type.SearchByType;
import com.wanted.teamV.type.SnsType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.*;

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

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PostHistoryRepository postHistoryRepository;

    @Mock
    private RestTemplate restTemplate;

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
        SearchByType searchBy = SearchByType.TITLE;
        String search = "성수동";
        int page = 0;
        int pageCount = 10;
        Pageable pageable = PageRequest.of(page, pageCount, Sort.by(Sort.Direction.DESC, "likeCount"));

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

        List<Post> postList = Collections.singletonList(post1);
        Page<Post> postPage = new PageImpl<>(postList, pageable, postList.size());

        List<Long> postIds = Arrays.asList(post1.getId(), post2.getId());
        when(postHashtagRepository.findPostIdsByHashtag(hashtag)).thenReturn(postIds);

        when(postRepository.filterPosts(postIds, type, searchBy, search, pageable)).thenReturn(postPage);

        // when
        Page<PostResDto> response = postService.getPosts(hashtag, type, searchBy, search, pageable);

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
        SearchByType searchBy = SearchByType.TITLE;
        String search = "명동";
        int page = 0;
        int pageCount = 10;
        Pageable pageable = PageRequest.of(page, pageCount, Sort.by(Sort.Direction.DESC, "createdAt"));

        postHashtagRepository.findPostIdsByHashtag(hashtag);

        // when & then
        CustomException customException = assertThrows(CustomException.class,
                () -> postService.getPosts(hashtag, type, searchBy, search, pageable));
        assertEquals(ErrorCode.NO_RELATED_POSTS_FOUND, customException.getErrorCode());
    }

    @Test
    @DisplayName("게시물 상세 조회 - 성공")
    public void getPostDetails() throws Exception {
        Post post = Post.builder()
                .contentId("123")
                .title("Test Post")
                .content("This is a test post.")
                .type(SnsType.FACEBOOK)
                .viewCount(0)
                .likeCount(0)
                .shareCount(0)
                .build();

        Member member = Member.builder().build();
        List<String> hashtags = List.of("맛집", "서울");

        PostHistory postHistory = PostHistory.builder()
                .post(post)
                .member(member)
                .type(HistoryType.VIEW)
                .build();

        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(postHashtagRepository.findHashTagsByPostId(post.getId())).thenReturn(hashtags);
        when(postHistoryRepository.save(Mockito.any(PostHistory.class))).thenReturn(postHistory);

        //when
        PostDetailResDto resDto = postService.getPostDetail(post.getId(), member.getId());

        //then
        assertEquals(post.getId(), resDto.getId());
        assertEquals(post.getType(), resDto.getType());
        assertEquals(post.getTitle(), resDto.getTitle());
        assertEquals(post.getViewCount(), resDto.getViewCount());
        assertEquals(hashtags, resDto.getPostHashtags());
    }

    @Test
    @DisplayName("게시물 상세 조회 - 실패(게시물 없음)")
    public void getPostDetails_No_Post() throws Exception {
        //given
        Long postId = 2L;
        Long memberId = 1L;

        //when & then
        CustomException customException = assertThrows(CustomException.class,
                () -> postService.getPostDetail(postId, memberId));
        assertEquals(ErrorCode.POST_NOT_FOUND, customException.getErrorCode());
    }

    @Test
    @DisplayName("게시물 좋아요 - 성공")
    public void increaseLike() throws Exception {
        //given
        Long postId = 1L, memberId = 1L;

        Post post1 = Post.testPostEntity();
        Member member = mock(Member.class);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post1));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(member.getId()).thenReturn(memberId);

        ResponseEntity<String> apiResponse = new ResponseEntity<>("OK", HttpStatus.OK);
        when(restTemplate.postForEntity(any(URI.class), any(), eq(String.class))).thenReturn(apiResponse);

        //when
        ResponseEntity<?> result = postService.increaseLike(post1.getId(), member.getId());

        //then
        assertEquals(apiResponse.getStatusCode(), result.getStatusCode());
        assertEquals(1, post1.getLikeCount());
    }

    @Test
    @DisplayName("게시물 좋아요 - 실패(게시물 없음)")
    public void increaseLike_No_Post() throws Exception {
        //given
        Long postId = 2L;
        Long memberId = 1L;

        //when & then
        CustomException customException = assertThrows(CustomException.class,
                () -> postService.increaseLike(postId, memberId));
        assertEquals(ErrorCode.POST_NOT_FOUND, customException.getErrorCode());
    }

    @Test
    @DisplayName("게시물 공유 - 성공")
    public void increaseShare() throws Exception {
        //given
        Long postId = 1L, memberId = 1L;

        Post post1 = Post.testPostEntity();
        Member member = mock(Member.class);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post1));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(member.getId()).thenReturn(memberId);

        ResponseEntity<String> apiResponse = new ResponseEntity<>("OK", HttpStatus.OK);
        when(restTemplate.postForEntity(any(URI.class), any(), eq(String.class))).thenReturn(apiResponse);

        //when
        ResponseEntity<?> result = postService.increaseShare(post1.getId(), member.getId());

        //then
        assertEquals(apiResponse.getStatusCode(), result.getStatusCode());
        assertEquals(1, post1.getShareCount());
    }

    @Test
    @DisplayName("게시물 공유 - 실패(게시물 없음)")
    public void increaseShare_No_Post() throws Exception {
        //given
        Long postId = 2L;
        Long memberId = 1L;

        //when & then
        CustomException customException = assertThrows(CustomException.class,
                () -> postService.increaseShare(postId, memberId));
        assertEquals(ErrorCode.POST_NOT_FOUND, customException.getErrorCode());
    }
}