package com.wanted.teamV.service;

import com.wanted.teamV.dto.res.PostDetailResDto;

public interface PostService {
    PostDetailResDto getPostDetail(Long postId, Long memberId);
}
