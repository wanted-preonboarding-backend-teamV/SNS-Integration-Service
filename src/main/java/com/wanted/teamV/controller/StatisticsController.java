package com.wanted.teamV.controller;

import com.wanted.teamV.dto.res.StatisticsResDto;
import com.wanted.teamV.service.StatisticsService;
import com.wanted.teamV.type.StatisticsSortType;
import com.wanted.teamV.type.StatisticsTimeType;
import com.wanted.teamV.type.StatisticsValueType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    private final StatisticsService statisticsService;

    @GetMapping
    public ResponseEntity<List<StatisticsResDto>> getStatistics(
        @RequestParam(value = "hashtag", required = false) String hashtag,
        @RequestParam(value = "start", required = false) LocalDate startDate,
        @RequestParam(value = "end", required = false) LocalDate endDate,
        @RequestParam(value = "type", required = false, defaultValue = "date") StatisticsTimeType timeType,
        @RequestParam(value = "value", required = false, defaultValue = "count") StatisticsValueType valueType,
        @RequestParam(value = "sort", required = false, defaultValue = "desc") StatisticsSortType sortType

    ) {

        if (hashtag == null || hashtag.isBlank()) {
            // TODO - JWT 토큰에서 본인 계정 불러오기
            hashtag = "맛집";
        }
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(7);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        List<StatisticsResDto> responses =
                statisticsService.getCountsForEachTimeByHashtag(hashtag, timeType, startDate, endDate, valueType, sortType);

        return ResponseEntity.ok(responses);
    }

}
