package com.wanted.teamV.service.impl;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.wanted.teamV.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostHashtagRepository postHashtagRepository;

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
     * @param hashtag : default = 사용자 account, 다른 해시태그로 검색 가능
     * @param type : 게시물이 올라와 있는 플랫폼 (ex. instagram)
     * @param orderBy : 정렬 순서 (default = created_at_desc) 날짜, 조회수, 좋아요수, 공유수
     * @param searchBy : 검색어가 제목, 내용 또는 제목&내용(default)에 포함되어 있는 게시물만 검색
     * @param search : 검색어
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

}