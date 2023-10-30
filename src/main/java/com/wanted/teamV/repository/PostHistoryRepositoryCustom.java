package com.wanted.teamV.repository;

import com.wanted.teamV.type.HistoryType;
import com.wanted.teamV.type.StatisticsTimeType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface PostHistoryRepositoryCustom {
    Map<String, Long> countsByTypeInPostIdsGroupByTimeType(HistoryType type, List<Long> postIds,
                                                           LocalDateTime startDateTime, LocalDateTime endDateTime, StatisticsTimeType timeType);
}
