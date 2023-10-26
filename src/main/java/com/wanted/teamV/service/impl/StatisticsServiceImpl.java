package com.wanted.teamV.service.impl;

import com.wanted.teamV.dto.res.StatisticsResDto;
import com.wanted.teamV.service.StatisticsService;
import com.wanted.teamV.type.StatisticsSortType;
import com.wanted.teamV.type.StatisticsTimeType;
import com.wanted.teamV.type.StatisticsValueType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsServiceImpl implements StatisticsService {
    @Override
    public List<StatisticsResDto> getCountsForEachTimeByHashtag(String hashtag, StatisticsTimeType timeType, LocalDate startDate, LocalDate endDate, StatisticsValueType valueType, StatisticsSortType sortType) {
        return List.of(
                new StatisticsResDto(timeType, LocalDateTime.of(startDate, LocalTime.MIN), 10),
                new StatisticsResDto(timeType, LocalDateTime.of(startDate.minusDays(1), LocalTime.MIN.plusHours(1)), 11),
                new StatisticsResDto(timeType, LocalDateTime.of(startDate.minusDays(2), LocalTime.MIN.plusHours(2)), 12),
                new StatisticsResDto(timeType, LocalDateTime.of(startDate.minusDays(3), LocalTime.MIN.plusHours(3)), 13)
        );
    }
}
