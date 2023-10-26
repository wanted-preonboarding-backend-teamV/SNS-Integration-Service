package com.wanted.teamV.repository;

import com.wanted.teamV.type.StatisticsTimeType;

import java.time.LocalDateTime;
import java.util.Map;

public interface PostRepositoryCustom {
    Map<String, Long> countsByHashtagGroupByTimeType(String hashtag, LocalDateTime startDateTime, LocalDateTime endDateTime, StatisticsTimeType timeType);
}
