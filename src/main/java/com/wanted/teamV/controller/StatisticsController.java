package com.wanted.teamV.controller;

import com.wanted.teamV.dto.res.StatisticsResDto;
import com.wanted.teamV.entity.Member;
import com.wanted.teamV.exception.CustomException;
import com.wanted.teamV.exception.ErrorCode;
import com.wanted.teamV.repository.MemberRepository;
import com.wanted.teamV.service.MemberService;
import com.wanted.teamV.service.StatisticsService;
import com.wanted.teamV.type.StatisticsSortType;
import com.wanted.teamV.type.StatisticsTimeType;
import com.wanted.teamV.type.StatisticsValueType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private static final int MAX_DATE_PERIOD = 31;
    private static final int MAX_HOUR_PERIOD = 7;

    private final StatisticsService statisticsService;
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @GetMapping
    public ResponseEntity<List<StatisticsResDto>> getStatistics(
        @RequestHeader(value = "Authorization", required = false) String authToken,
        @RequestParam(value = "hashtag", required = false) String hashtag,
        @RequestParam(value = "start", required = false) LocalDate startDate,
        @RequestParam(value = "end", required = false) LocalDate endDate,
        @RequestParam(value = "type", required = false, defaultValue = "date") StatisticsTimeType timeType,
        @RequestParam(value = "value", required = false, defaultValue = "count") StatisticsValueType valueType,
        @RequestParam(value = "sort", required = false, defaultValue = "desc") StatisticsSortType sortType

    ) {

        if (hashtag == null || hashtag.isBlank()) {
            hashtag = getMemberAccount(authToken);
        }

        if (startDate == null && endDate == null) {
            startDate = LocalDate.now().minusDays(MAX_HOUR_PERIOD -1);
            endDate = LocalDate.now();
        }
        else if (startDate == null) {
            startDate = endDate.minusDays(MAX_HOUR_PERIOD -1);
        }
        else if (endDate == null) {
            endDate = startDate.plusDays(MAX_HOUR_PERIOD -1);
        }

        if (endDate.isAfter(LocalDate.now())) {
            endDate = LocalDate.now();
        }

        if (startDate.isAfter(endDate) || exceedMaxPeriod(startDate, endDate, timeType)) {
            throw new CustomException(ErrorCode.INVALID_STATISTICS_DATE);
        }

        List<StatisticsResDto> responses =
                statisticsService.getCountsForEachTimeByHashtag(hashtag, timeType, startDate, endDate, valueType, sortType);

        return ResponseEntity.ok(responses);
    }

    private String getMemberAccount(String authToken) {
        if (authToken == null || authToken.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
        Long memberId = memberService.extractUserId(authToken);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REQUEST));
        return member.getAccount();
    }

    private boolean exceedMaxPeriod(LocalDate startDate, LocalDate endDate, StatisticsTimeType timeType) {
        return (timeType == StatisticsTimeType.DATE && startDate.until(endDate, ChronoUnit.DAYS) > MAX_DATE_PERIOD - 1)
                || (timeType == StatisticsTimeType.HOUR && startDate.until(endDate, ChronoUnit.DAYS) > MAX_HOUR_PERIOD - 1);
    }

}
