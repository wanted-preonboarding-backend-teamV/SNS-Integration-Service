package com.wanted.teamV.service.impl;

import com.wanted.teamV.dto.res.StatisticsResDto;
import com.wanted.teamV.entity.Post;
import com.wanted.teamV.entity.PostHashtag;
import com.wanted.teamV.repository.PostHashtagRepository;
import com.wanted.teamV.repository.PostHistoryRepository;
import com.wanted.teamV.repository.PostRepository;
import com.wanted.teamV.type.StatisticsSortType;
import com.wanted.teamV.type.StatisticsTimeType;
import com.wanted.teamV.type.StatisticsValueType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceImplTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private PostHashtagRepository hashtagRepository;
    @Mock
    private PostHistoryRepository historyRepository;
    @InjectMocks
    private StatisticsServiceImpl statisticsService;

    @Test
    @DisplayName("날짜별 통계 정보(조회수/좋아요/공유)를 불러온다.")
    public void get_statistics_with_date() {
        // given
        String hashtag = "맛집";
        StatisticsTimeType timeType = StatisticsTimeType.DATE;
        LocalDate startDate = LocalDate.of(2023, 10, 20);
        LocalDate endDate = LocalDate.of(2023, 10, 23);
        StatisticsValueType valueType = StatisticsValueType.LIKE_COUNT;
        StatisticsSortType sortType = StatisticsSortType.DESC;

        PostHashtag postHashtag = mock(PostHashtag.class);
        Post post = mock(Post.class);
        when(postHashtag.getPost()).thenReturn(post);
        when(post.getId()).thenReturn(1L);
        when(hashtagRepository.findAllByHashtag(hashtag))
                .thenReturn(List.of(postHashtag));

        when(historyRepository.countsByTypeInPostIdsGroupByTimeType(any(), anyList(), any(), any(), eq(timeType)))
                .thenReturn(Map.of("2023-10-20", 1L, "2023-10-22", 2L));

        // when
        List<StatisticsResDto> responses = statisticsService.getCountsForEachTimeByHashtag(
                hashtag, timeType, startDate, endDate, valueType, sortType);

        // then
        Assertions.assertEquals("2023-10-23", responses.get(0).getTime());
        Assertions.assertEquals("2023-10-22", responses.get(1).getTime());
        Assertions.assertEquals("2023-10-21", responses.get(2).getTime());
        Assertions.assertEquals("2023-10-20", responses.get(3).getTime());
        Assertions.assertEquals(0, responses.get(0).getValue());
        Assertions.assertEquals(2L, responses.get(1).getValue());
        Assertions.assertEquals(0, responses.get(2).getValue());
        Assertions.assertEquals(1L, responses.get(3).getValue());
    }

    @Test
    @DisplayName("날짜+시간별 통계 정보(조회수/좋아요/공유)를 불러온다.")
    public void get_statistics_with_hour() {
        // given
        String hashtag = "맛집";
        StatisticsTimeType timeType = StatisticsTimeType.HOUR;
        LocalDate startDate = LocalDate.of(2023, 10, 20);
        LocalDate endDate = LocalDate.of(2023, 10, 21);
        StatisticsValueType valueType = StatisticsValueType.LIKE_COUNT;
        StatisticsSortType sortType = StatisticsSortType.DESC;

        PostHashtag postHashtag = mock(PostHashtag.class);
        Post post = mock(Post.class);
        when(postHashtag.getPost()).thenReturn(post);
        when(post.getId()).thenReturn(1L);
        when(hashtagRepository.findAllByHashtag(hashtag))
                .thenReturn(List.of(postHashtag));

        when(historyRepository.countsByTypeInPostIdsGroupByTimeType(any(), anyList(), any(), any(), eq(timeType)))
                .thenReturn(Map.of("2023-10-20-02", 1L, "2023-10-21-13", 2L));

        // when
        List<StatisticsResDto> responses = statisticsService.getCountsForEachTimeByHashtag(
                hashtag, timeType, startDate, endDate, valueType, sortType);

        // then
        Assertions.assertEquals("2023-10-21T13:00:00", responses.get(13).getTime());
        Assertions.assertEquals("2023-10-20T02:00:00", responses.get(26).getTime());
        Assertions.assertEquals(2L, responses.get(13).getValue());
        Assertions.assertEquals(1L, responses.get(26).getValue());
    }

    @Test
    @DisplayName("날짜별 통계 정보(게시물)를 불러온다.")
    public void get_post_count_statistics_with_date() {
        // given
        String hashtag = "맛집";
        StatisticsTimeType timeType = StatisticsTimeType.DATE;
        LocalDate startDate = LocalDate.of(2023, 10, 20);
        LocalDate endDate = LocalDate.of(2023, 10, 23);
        StatisticsValueType valueType = StatisticsValueType.COUNT;
        StatisticsSortType sortType = StatisticsSortType.DESC;

        when(postRepository.countsByHashtagGroupByTimeType(any(), any(), any(), eq(timeType)))
                .thenReturn(Map.of("2023-10-20", 1L, "2023-10-22", 2L));

        // when
        List<StatisticsResDto> responses = statisticsService.getCountsForEachTimeByHashtag(
                hashtag, timeType, startDate, endDate, valueType, sortType);

        // then
        Assertions.assertEquals("2023-10-23", responses.get(0).getTime());
        Assertions.assertEquals("2023-10-22", responses.get(1).getTime());
        Assertions.assertEquals("2023-10-21", responses.get(2).getTime());
        Assertions.assertEquals("2023-10-20", responses.get(3).getTime());
        Assertions.assertEquals(0, responses.get(0).getValue());
        Assertions.assertEquals(2L, responses.get(1).getValue());
        Assertions.assertEquals(0, responses.get(2).getValue());
        Assertions.assertEquals(1L, responses.get(3).getValue());
    }

    @Test
    @DisplayName("날짜+시간별 통계 정보(게시물)를 불러온다.")
    public void get_post_count_statistics_with_hour() {
        // given
        String hashtag = "맛집";
        StatisticsTimeType timeType = StatisticsTimeType.HOUR;
        LocalDate startDate = LocalDate.of(2023, 10, 20);
        LocalDate endDate = LocalDate.of(2023, 10, 21);
        StatisticsValueType valueType = StatisticsValueType.COUNT;
        StatisticsSortType sortType = StatisticsSortType.DESC;

        when(postRepository.countsByHashtagGroupByTimeType(any(), any(), any(), eq(timeType)))
                .thenReturn(Map.of("2023-10-20-02", 1L, "2023-10-21-13", 2L));

        // when
        List<StatisticsResDto> responses = statisticsService.getCountsForEachTimeByHashtag(
                hashtag, timeType, startDate, endDate, valueType, sortType);

        // then
        Assertions.assertEquals("2023-10-21T13:00:00", responses.get(13).getTime());
        Assertions.assertEquals("2023-10-20T02:00:00", responses.get(26).getTime());
        Assertions.assertEquals(2L, responses.get(13).getValue());
        Assertions.assertEquals(1L, responses.get(26).getValue());
    }

}