package com.wanted.teamV.service.impl;


import com.wanted.teamV.dto.res.PostDetailResDto;
import com.wanted.teamV.entity.Member;
import com.wanted.teamV.entity.PostHistory;
import com.wanted.teamV.repository.MemberRepository;
import com.wanted.teamV.repository.PostHistoryRepository;
import com.wanted.teamV.type.HistoryType;

import com.wanted.teamV.dto.req.PostCreateReqDto;
import com.wanted.teamV.dto.res.ListResDto;
import com.wanted.teamV.dto.res.PostResDto;
import com.wanted.teamV.entity.Post;
import com.wanted.teamV.entity.PostHashtag;
import com.wanted.teamV.exception.CustomException;
import com.wanted.teamV.exception.ErrorCode;
import com.wanted.teamV.repository.PostHashtagRepository;
import com.wanted.teamV.repository.PostRepository;
import com.wanted.teamV.service.PostService;
import com.wanted.teamV.type.OrderByType;
import com.wanted.teamV.type.SearchByType;
import com.wanted.teamV.type.SnsType;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import java.util.stream.Collectors;

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

    // 게시물 생성 메서드
    @Override
    @Transactional
    public void createPost(PostCreateReqDto request) {
        if (request.getContentId().isBlank() || request.getTitle().isBlank() || request.getContent().isBlank()) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        Post post = Post.builder()
                .contentId(request.getContentId())
                .title(request.getTitle())
                .content(request.getContent())
                .type(SnsType.parse(request.getType()))
                .viewCount(request.getViewCount())
                .likeCount(request.getLikeCount())
                .shareCount(request.getShareCount())
                .build();

        postRepository.save(post);

        for (String hashtag : request.getHashtags()) {
            PostHashtag postHashtag = PostHashtag.builder()
                    .post(post)
                    .hashtag(hashtag)
                    .build();
            postHashtagRepository.save(postHashtag);
        }
    }

    /**
     * 게시물 조회 메서드
     *
     * @param hashtag  : default = 사용자 account, 다른 해시태그로 검색 가능
     * @param type     : 게시물이 올라와 있는 플랫폼 (ex. instagram)
     * @param orderBy  : 정렬 순서 (default = created_at_desc) 날짜, 조회수, 좋아요수, 공유수
     * @param searchBy : 검색어가 제목, 내용 또는 제목&내용(default)에 포함되어 있는 게시물만 검색
     * @param search   : 검색어
     * @param pageable : 한 페이지에 보여줄 게시물 개수 및 현재 보여줄 페이지
     * @return
     */
    @Override
    @Transactional
    public ListResDto<PostResDto> getPosts(String hashtag, SnsType type, OrderByType orderBy, SearchByType searchBy,
                                           String search, Pageable pageable) {

        List<Long> postIds = postHashtagRepository.findPostIdsByHashtag(hashtag);

        if (postIds == null || postIds.size() == 0) {
            throw new CustomException(NO_RELATED_POSTS_FOUND);
        }

        List<Post> filteredPosts = postRepository.filterPosts(postIds, type, orderBy, searchBy, search);

        if (filteredPosts == null || filteredPosts.size() == 0) {
            throw new CustomException(NO_RELATED_POSTS_FOUND);
        }

        // 페이지네이션 적용
        int totalElements = filteredPosts.size();
        int fromIndex = (int) pageable.getOffset();
        int pageSize = pageable.getPageSize();
        int toIndex = Math.min(fromIndex + pageSize, totalElements);

        if (fromIndex < 0 || fromIndex >= totalElements || pageSize <= 0) {
            throw new CustomException(INVALID_PAGE_REQUEST);
        }

        List<Post> pagedPosts = filteredPosts.subList(fromIndex, toIndex);

        // PostResDto로 변환
        List<PostResDto> postResDtos = pagedPosts.stream()
                .map(post -> {
                    PostResDto postResDto = PostResDto.mapToPostResDto(post);
                    if (postResDto.getContent() != null && postResDto.getContent().length() > 20) {
                        postResDto.setContent(postResDto.getContent().substring(0, 20));
                    }
                    return postResDto;
                })
                .collect(Collectors.toList());

        // ListResDto 생성
        ListResDto<PostResDto> response = new ListResDto<>();
        response.setContent(postResDtos);
        response.setPageable(createPageable(pageable));
        response.setLast(pageable.getPageNumber() >= (totalElements / pageSize));
        response.setTotalPages((long) Math.ceil((double) totalElements / pageSize));
        response.setTotalElements(totalElements);
        response.setSize(pageable.getPageSize());
        response.setNumber(pageable.getPageNumber());
        response.setSort(createSort(pageable.getSort()));
        response.setNumberOfElements(pagedPosts.size());
        response.setFirst(pageable.first().isPaged());
        response.setEmpty(pagedPosts.isEmpty());

        return response;
    }

    private ListResDto.Pageable createPageable(Pageable pageable) {
        ListResDto.Pageable result = new ListResDto.Pageable();
        result.setSort(createSort(pageable.getSort()));
        result.setOffset(pageable.getOffset());
        result.setPageSize(pageable.getPageSize());
        result.setPageNumber(pageable.getPageNumber());
        result.setPaged(pageable.isPaged());
        result.setUnpaged(pageable.isUnpaged());

        return result;
    }

    private ListResDto.Sort createSort(Sort sort) {
        ListResDto.Sort result = new ListResDto.Sort();
        result.setSorted(sort.isSorted());
        result.setUnsorted(sort.isUnsorted());
        result.setEmpty(sort.isEmpty());

        return result;
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
