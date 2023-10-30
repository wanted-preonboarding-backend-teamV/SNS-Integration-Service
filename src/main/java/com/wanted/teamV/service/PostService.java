package com.wanted.teamV.service;

import com.wanted.teamV.dto.req.PostCreateReqDto;
import com.wanted.teamV.dto.res.PostDetailResDto;
import com.wanted.teamV.dto.res.PostResDto;
import com.wanted.teamV.type.SearchByType;
import com.wanted.teamV.type.SnsType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface PostService {
    // 게시물 생성 메서드
    void createPost(PostCreateReqDto request);

    Page<PostResDto> getPosts(String hashtag, SnsType type, SearchByType searchBy, String search, Pageable pageable);
    PostDetailResDto getPostDetail(Long postId, Long memberId);

    ResponseEntity<?> increaseLike(Long postId, Long memberId);

    ResponseEntity<?> increaseShare(Long postId, Long memberId);
}
