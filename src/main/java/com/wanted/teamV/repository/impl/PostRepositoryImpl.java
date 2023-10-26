package com.wanted.teamV.repository.impl;

import com.querydsl.core.types.Constant;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wanted.teamV.repository.PostRepositoryCustom;
import com.wanted.teamV.type.StatisticsTimeType;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import static com.wanted.teamV.entity.QPost.post;
import static com.wanted.teamV.entity.QPostHashtag.postHashtag;
import static com.wanted.teamV.type.StatisticsTimeType.DATE;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {
    private final JPAQueryFactory queryFactory;

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
