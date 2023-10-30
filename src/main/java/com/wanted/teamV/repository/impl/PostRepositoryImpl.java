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

import com.querydsl.core.types.Constant;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.wanted.teamV.type.StatisticsTimeType;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import static com.wanted.teamV.entity.QPost.post;
import static com.wanted.teamV.entity.QPostHashtag.postHashtag;
import static com.wanted.teamV.type.StatisticsTimeType.DATE;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    // postIds 중에서 type, orderBy, searchBy, search 조건에 맞는 Post 객체 리스트 반환
    @Override
    public List<Post> filterPosts(List<Long> postIds, SnsType type, OrderByType orderBy, SearchByType searchBy, String search) {
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

    // 해시태그, 작성된 날짜로 게시물을 조회하여 날짜/시간별 카운트를 구함
    @Override
    public Map<String, Long> countsByHashtagGroupByTimeType(String hashtag, LocalDateTime startDateTime, LocalDateTime endDateTime, StatisticsTimeType timeType) {
        StringTemplate formattedDate = getFormattedDate(timeType);
        return queryFactory
                .select(formattedDate, postHashtag.post.count())
                .from(postHashtag)
                .where(
                        postHashtag.hashtag.eq(hashtag),
                        postHashtag.post.createdAt.between(startDateTime, endDateTime)
                )
                .groupBy(formattedDate)
                .fetch()
                .stream().collect(
                        Collectors.toMap(
                                tuple -> tuple.get(formattedDate),
                                tuple -> tuple.get(postHashtag.post.count())
                        )
                );
    }

    private static StringTemplate getFormattedDate(StatisticsTimeType timeType) {
        Constant<String> formatConstant = timeType == DATE ? ConstantImpl.create("%Y-%m-%d") : ConstantImpl.create("%Y-%m-%d-%h");

        return Expressions.stringTemplate(
                "DATE_FORMAT({0},{1})",
                post.createdAt,
                formatConstant);
    }
}