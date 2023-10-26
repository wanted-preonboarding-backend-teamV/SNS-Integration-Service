package com.wanted.teamV.service.impl;

import com.wanted.teamV.dto.res.StatisticsResDto;
import com.wanted.teamV.repository.PostHashtagRepository;
import com.wanted.teamV.repository.PostHistoryRepository;
import com.wanted.teamV.repository.PostRepository;
import com.wanted.teamV.service.StatisticsService;
import com.wanted.teamV.type.HistoryType;
import com.wanted.teamV.type.StatisticsSortType;
import com.wanted.teamV.type.StatisticsTimeType;
import com.wanted.teamV.type.StatisticsValueType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsServiceImpl implements StatisticsService {

    private final PostRepository postRepository;
    private final PostHistoryRepository historyRepository;
    private final PostHashtagRepository postHashtagRepository;

    @Override
    public List<StatisticsResDto> getCountsForEachTimeByHashtag(String hashtag, StatisticsTimeType timeType, LocalDate startDate, LocalDate endDate, StatisticsValueType valueType, StatisticsSortType sortType) {
        Map<String, Long> historyCounts;
        if (valueType == StatisticsValueType.COUNT) {
            historyCounts = postRepository.countsByHashtagGroupByTimeType(
                    hashtag,
                    startDate.atStartOfDay(),
                    endDate.atTime(LocalTime.MAX),
                    timeType);
        } else {
            List<Long> postIds = postHashtagRepository.findAllByHashtag(hashtag)
                    .stream().map(it -> it.getPost().getId()).toList();

            historyCounts = historyRepository.countsByTypeInPostIdsGroupByTimeType(
                    HistoryType.parseStatisticsValueType(valueType),
                    postIds,
                    startDate.atStartOfDay(),
                    endDate.atTime(LocalTime.MAX),
                    timeType);
        }

        Map<LocalDateTime, String> dateTimesAndKeys = getDateTimesAndKeysWithOrder(timeType, startDate, endDate, sortType);

        List<StatisticsResDto> results = new ArrayList<>();
        dateTimesAndKeys.forEach((dateTime, key) -> {
            long count = historyCounts.containsKey(key) ? historyCounts.get(key) : 0;
            results.add(new StatisticsResDto(timeType, dateTime, count));
        });
        return results;
    }

    // 시작날짜부터 종료날짜까지를 날짜 또는 시간마다 쪼개서 정렬하고, DB조회 결과와 매칭할 형식과 함께 Map으로 반환하는 함수
    private static Map<LocalDateTime, String> getDateTimesAndKeysWithOrder(StatisticsTimeType timeType,
                                                                           LocalDate startDate, LocalDate endDate, StatisticsSortType sortType) {
        Map<LocalDateTime, String> dateTimes = new LinkedHashMap<>();

        Stream<LocalDate> dateStream = startDate.datesUntil(endDate.plusDays(1));
        if (sortType == StatisticsSortType.DESC) {
            dateStream = dateStream.sorted(Comparator.reverseOrder());
        }

        dateStream.forEach(date -> {
            if (timeType == StatisticsTimeType.HOUR) {
                for (int i = 0; i < 24; i++) {
                    LocalDateTime dateTime = date.atTime(i, 0);
                    dateTimes.put(dateTime, dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-hh")));
                }
            } else {
                dateTimes.put(date.atStartOfDay(), date.format(DateTimeFormatter.ISO_LOCAL_DATE));
            }
        });
        return dateTimes;
    }
}
