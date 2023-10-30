package com.wanted.teamV.repository.impl;

import com.querydsl.core.types.Constant;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wanted.teamV.entity.Post;
import com.wanted.teamV.entity.QPost;
import com.wanted.teamV.repository.PostRepositoryCustom;
import com.wanted.teamV.type.SearchByType;
import com.wanted.teamV.type.SnsType;
import com.wanted.teamV.type.StatisticsTimeType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    public Page<Post> filterPosts(List<Long> postIds, SnsType type, SearchByType searchBy, String search, Pageable pageable) {
        QPost post = QPost.post;
        JPAQuery<Post> query = queryFactory.selectFrom(post);

        if (!postIds.isEmpty()) {
            query.where(post.id.in(postIds));
        }

        if (type != null) {
            query.where(post.type.eq(type));
        }

        if (searchBy != null && search != null && !search.isEmpty()) {
            if (searchBy.equals(SearchByType.CONTENT)) {
                query.where(post.content.containsIgnoreCase(search));
            } else if (searchBy.equals(SearchByType.TITLE)) {
                query.where(post.title.containsIgnoreCase(search));
            } else if (searchBy.equals(SearchByType.BOTH)) {
                query.where(post.content.containsIgnoreCase(search)
                        .or(post.title.containsIgnoreCase(search)));
            }
        }

        List<OrderSpecifier> order = new ArrayList<>();
        pageable.getSort().stream().forEach(o -> {
            order.add(new OrderSpecifier((o.getDirection().isDescending() ? Order.DESC : Order.ASC),
                    new PathBuilder(Post.class, "post").get(o.getProperty())));
        });

        query = query.offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(order.stream().toArray((OrderSpecifier[]::new)));

        return PageableExecutionUtils.getPage(query.fetch(), pageable, query::fetchCount);
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