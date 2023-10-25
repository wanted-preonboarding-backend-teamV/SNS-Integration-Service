package com.wanted.teamV.controller;

import com.wanted.teamV.dto.res.StatisticsResDto;
import com.wanted.teamV.exception.CustomException;
import com.wanted.teamV.exception.ErrorCode;
import com.wanted.teamV.service.StatisticsService;
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
        @RequestParam(required = false) String hashtag,
        @RequestParam StatisticsTimeType type,
        @RequestParam(required = false) LocalDate start,
        @RequestParam(required = false) LocalDate end,
        @RequestParam(required = false, defaultValue = "count") StatisticsValueType value
    ) {

        if (hashtag == null || hashtag.isBlank()) {
            // TODO - JWT 토큰에서 본인 계정 불러오기
            hashtag = "맛집";
        }
        if (start == null) {
            start = LocalDate.now().minusDays(7);
        }
        if (end == null) {
            end = LocalDate.now();
        }

        List<StatisticsResDto> responses =
                statisticsService.getCountsForEachTimeByHashtag(hashtag, type, start, end, value);

        return ResponseEntity.ok(responses);
    }

}
