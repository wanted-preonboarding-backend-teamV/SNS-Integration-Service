package com.wanted.teamV.service;

import com.wanted.teamV.dto.res.StatisticsResDto;
import com.wanted.teamV.type.StatisticsSortType;
import com.wanted.teamV.type.StatisticsTimeType;
import com.wanted.teamV.type.StatisticsValueType;

import java.time.LocalDate;
import java.util.List;

public interface StatisticsService {
    List<StatisticsResDto> getCountsForEachTimeByHashtag(String hashtag, StatisticsTimeType timeType, LocalDate startDate, LocalDate endDate, StatisticsValueType valueType, StatisticsSortType sortType);
}
