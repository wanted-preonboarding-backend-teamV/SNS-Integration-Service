package com.wanted.teamV.service.impl;

import com.wanted.teamV.dto.res.PostDetailResDto;
import com.wanted.teamV.entity.Member;
import com.wanted.teamV.entity.Post;
import com.wanted.teamV.entity.PostHistory;
import com.wanted.teamV.repository.MemberRepository;
import com.wanted.teamV.repository.PostHashtagRepository;
import com.wanted.teamV.repository.PostHistoryRepository;
import com.wanted.teamV.repository.PostRepository;
import com.wanted.teamV.type.HistoryType;
import com.wanted.teamV.type.SnsType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Transactional
public class PostServiceImplTest {
    @Mock
    private PostRepository postRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PostHashtagRepository postHashtagRepository;

    @Mock
    private PostHistoryRepository postHistoryRepository;

    @InjectMocks
    private PostServiceImpl postService;

    @Test
    void getPostDetails() throws Exception {
        //given
        Long postId = 1L, memberId = 1L;
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

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(postHashtagRepository.findHashTagsByPostId(postId)).thenReturn(hashtags);
        when(postHistoryRepository.save(Mockito.any(PostHistory.class))).thenReturn(postHistory);

        //when
        PostDetailResDto resDto = postService.getPostDetail(postId, memberId);

        //then
        assertEquals(post.getId(), resDto.getId());
        assertEquals(post.getType(), resDto.getType());
        assertEquals(post.getTitle(), resDto.getTitle());
        assertEquals(post.getViewCount(), resDto.getViewCount());
        assertEquals(hashtags, resDto.getPostHashtags());
    }
}