package com.wanted.teamV.service.impl;

import com.wanted.teamV.dto.res.PostDetailResDto;
import com.wanted.teamV.entity.Member;
import com.wanted.teamV.entity.Post;
import com.wanted.teamV.entity.PostHashtag;
import com.wanted.teamV.entity.PostHistory;
import com.wanted.teamV.exception.CustomException;
import com.wanted.teamV.repository.MemberRepository;
import com.wanted.teamV.repository.PostHashtagRepository;
import com.wanted.teamV.repository.PostHistoryRepository;
import com.wanted.teamV.repository.PostRepository;
import com.wanted.teamV.service.PostService;
import com.wanted.teamV.type.HistoryType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.wanted.teamV.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PostHistoryRepository postHistoryRepository;
    private final PostHashtagRepository postHashtagRepository;

    //게시물 상세 조회
    @Override
    public PostDetailResDto getPostDetail(Long postId, Long memberId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new CustomException(POST_NOT_FOUND));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        List<String> postHashtags = postHashtagRepository.findHashTagsByPostId(postId);

        post.increaseViewCount();
        postRepository.save(post);

        PostHistory postHistory = PostHistory.builder()
                .post(post)
                .member(member)
                .type(HistoryType.VIEW)
                .build();

        postHistoryRepository.save(postHistory);

        PostDetailResDto responseDto = PostDetailResDto.builder()
                .id(post.getId())
                .contentId(post.getContentId())
                .title(post.getTitle())
                .content(post.getContent())
                .type(post.getType())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .shareCount(post.getShareCount())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .postHashtags(postHashtags)
                .build();

        return responseDto;
    }
}
