package com.wanted.teamV.service.impl;

import com.wanted.teamV.dto.res.PostDetailResDto;
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
import com.wanted.teamV.service.PostService;
import com.wanted.teamV.type.HistoryType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import static com.wanted.teamV.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PostHistoryRepository postHistoryRepository;
    private final PostHashtagRepository postHashtagRepository;
    private final RestTemplate restTemplate;

    //게시물 상세 조회
    @Override
    public PostDetailResDto getPostDetail(Long postId, Long memberId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        List<String> postHashtags;
        try {
            postHashtags = postHashtagRepository.findHashTagsByPostId(postId);
        } catch (Exception e) {
            e.printStackTrace();
            postHashtags = new ArrayList<>();
        }

        try {
            post.increaseViewCount();
            postRepository.save(post);
        } catch (Exception e) {
            e.printStackTrace();
        }

        PostHistory postHistory = PostHistory.builder()
                .post(post)
                .member(member)
                .type(HistoryType.VIEW)
                .build();

        try {
            postHistoryRepository.save(postHistory);
        } catch (Exception e) {
            e.printStackTrace();
        }

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

    @Override
    public ResponseEntity<?> increaseLike(Long postId, Long memberId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        String contentId = post.getContentId();
        String url = "";

        switch (post.getType()) {
            case FACEBOOK:
                url = "https://www.facebook.com";
                break;
            case INSTAGRAM:
                url = "https://www.instagram.com";
                break;
            case X:
                url = "https://www.twitter.com";
                break;
            case THREADS:
                url = "https://www.threads.com";
                break;
        }

        URI apiUri = UriComponentsBuilder
                .fromUriString(url)
                .path("/likes/{contendId}")
                .encode()
                .buildAndExpand(contentId)
                .toUri();

        ResponseEntity<?> response = null;

        try {
            response = restTemplate.postForEntity(apiUri, contentId, String.class);
            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.MOVED_PERMANENTLY) {
                post.increaseLikeCount();
            } else if (response == null) {
                response = ResponseEntity.status(HttpStatus.OK).body("OK");
            }
        } catch (HttpClientErrorException ex) {
            ex.printStackTrace();
            post.increaseLikeCount();
        }

        PostHistory postHistory = PostHistory.builder()
                .post(post)
                .member(member)
                .type(HistoryType.LIKE)
                .build();

        try {
            postHistoryRepository.save(postHistory);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }
}
