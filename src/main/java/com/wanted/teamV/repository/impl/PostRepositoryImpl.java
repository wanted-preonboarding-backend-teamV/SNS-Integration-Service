package com.wanted.teamV.repository.impl;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wanted.teamV.entity.Post;
import com.wanted.teamV.entity.QPost;
import com.wanted.teamV.repository.PostRepositoryCustom;
import com.wanted.teamV.type.OrderByType;
import com.wanted.teamV.type.SearchByType;
import com.wanted.teamV.type.SnsType;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    // postIds 중에서 type, orderBy, searchBy, search 조건에 맞는 Post 객체 리스트 반환
    @Override
    public List<Post> filterPosts(List<Long> postIds, SnsType type, OrderByType orderBy, SearchByType searchBy,
                                  String search) {
        QPost post = QPost.post;
        JPAQuery<Post> query = queryFactory.selectFrom(post);

        if (!postIds.isEmpty()) {
            // 게시물 ID 리스트를 이용한 필터링
            query.where(post.id.in(postIds));
        }

        if (type != null) {
            // SnsType에 따른 필터링 (type이 null이 아닐 때만)
            query.where(post.type.eq(type));
        }

        if (searchBy != null && search != null && !search.isEmpty()) {
            // 검색 조건에 따라 content 또는 title 또는 둘 다 탐색
            if (searchBy.equals(SearchByType.CONTENT)) {
                query.where(post.content.containsIgnoreCase(search));
            } else if (searchBy.equals(SearchByType.TITLE)) {
                query.where(post.title.containsIgnoreCase(search));
            } else if (searchBy.equals(SearchByType.BOTH)) {
                query.where(post.content.containsIgnoreCase(search)
                        .or(post.title.containsIgnoreCase(search)));
            }
        }

        if (orderBy != null) {
            // orderBy에 따른 정렬
            switch (orderBy) {
                case CREATED_AT_ASC:
                    query.orderBy(post.createdAt.asc());
                    break;
                case CREATED_AT_DESC:
                    query.orderBy(post.createdAt.desc());
                    break;
                case UPDATED_AT_ASC:
                    query.orderBy(post.updatedAt.asc());
                    break;
                case UPDATED_AT_DESC:
                    query.orderBy(post.updatedAt.desc());
                    break;
                case LIKE_COUNT_ASC:
                    query.orderBy(post.likeCount.asc());
                    break;
                case LIKE_COUNT_DESC:
                    query.orderBy(post.likeCount.desc());
                    break;
                case SHARE_COUNT_ASC:
                    query.orderBy(post.shareCount.asc());
                    break;
                case SHARE_COUNT_DESC:
                    query.orderBy(post.shareCount.desc());
                    break;
                case VIEW_COUNT_ASC:
                    query.orderBy(post.viewCount.asc());
                    break;
                case VIEW_COUNT_DESC:
                    query.orderBy(post.viewCount.desc());
                    break;
            }
        }

        return query.fetch();
    }

}

