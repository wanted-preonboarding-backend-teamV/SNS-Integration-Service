package com.wanted.teamV.service;

import com.wanted.teamV.dto.req.PostCreateReqDto;
import com.wanted.teamV.dto.res.ListResDto;
import com.wanted.teamV.dto.res.PostResDto;
import com.wanted.teamV.type.OrderByType;
import com.wanted.teamV.type.SearchByType;
import com.wanted.teamV.type.SnsType;
import org.springframework.data.domain.Pageable;

public interface PostService {
    // 게시물 생성 메서드
    void createPost(PostCreateReqDto request);

    ListResDto<PostResDto> getPosts(String hashtag, SnsType type, OrderByType orderByType, SearchByType searchBy,
                                          String search, Pageable pageable);
}
