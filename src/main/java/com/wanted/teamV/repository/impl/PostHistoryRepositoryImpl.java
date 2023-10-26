package com.wanted.teamV.repository.impl;

import com.querydsl.core.types.Constant;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wanted.teamV.repository.PostHistoryRepositoryCustom;
import com.wanted.teamV.type.HistoryType;
import com.wanted.teamV.type.StatisticsTimeType;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.wanted.teamV.entity.QPostHistory.postHistory;
import static com.wanted.teamV.type.StatisticsTimeType.DATE;

@RequiredArgsConstructor
public class PostHistoryRepositoryImpl implements PostHistoryRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    // 기록 타입, 게시물 아이디 목록, 기간으로 기록을 조회하여 날짜/시간별 카운트를 구함
    @Override
    public Map<String, Long> countsByTypeInPostIdsGroupByTimeType(HistoryType type, List<Long> postIds,
                                                                  LocalDateTime startDateTime, LocalDateTime endDateTime, StatisticsTimeType timeType) {
        StringTemplate formattedDate = getFormattedDate(timeType);
        return queryFactory
                .select(formattedDate, postHistory.count())
                .from(postHistory)
                .where(
                        postHistory.post.id.in(postIds),
                        postHistory.type.eq(type),
                        postHistory.createdAt.between(startDateTime, endDateTime)
                )
                .groupBy(formattedDate)
                .fetch()
                .stream().collect(
                        Collectors.toMap(
                                tuple -> tuple.get(formattedDate),
                                tuple -> tuple.get(postHistory.count())
                        )
                );
    }

    // 시간 형식에 맞춰 MySQL 쿼리에 사용할 템플릿 객체 생성 (group by시 사용됨)
    private static StringTemplate getFormattedDate(StatisticsTimeType timeType) {
        Constant<String> formatConstant = timeType == DATE ? ConstantImpl.create("%Y-%m-%d") : ConstantImpl.create("%Y-%m-%d-%h");

        return Expressions.stringTemplate(
                "DATE_FORMAT({0},{1})",
                postHistory.createdAt,
                formatConstant);
    }
}
