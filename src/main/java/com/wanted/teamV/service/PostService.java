package com.wanted.teamV.service;

import com.wanted.teamV.dto.res.PostDetailResDto;
import org.springframework.http.ResponseEntity;

public interface PostService {
    PostDetailResDto getPostDetail(Long postId, Long memberId);

    ResponseEntity<?> increaseLike(Long postId, Long memberId);

    ResponseEntity<?> increaseShare(Long postId, Long memberId);

}
