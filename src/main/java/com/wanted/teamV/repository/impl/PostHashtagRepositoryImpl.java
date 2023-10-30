package com.wanted.teamV.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wanted.teamV.repository.PostHashtagRepositoryCustom;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.wanted.teamV.entity.QPostHashtag.postHashtag;

@RequiredArgsConstructor
public class PostHashtagRepositoryImpl implements PostHashtagRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    // 해시태그로 PostHashtag를 조회하여 게시물 Id 리스트 반환
    @Override
    public List<Long> findPostIdsByHashtag(String hashtag) {
        return queryFactory.select(postHashtag.post.id)
                .from(postHashtag)
                .where(postHashtag.hashtag.eq(hashtag))
                .fetch();
    }

}

