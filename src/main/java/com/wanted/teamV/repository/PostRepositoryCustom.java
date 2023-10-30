package com.wanted.teamV.repository;

import com.wanted.teamV.entity.Post;
import com.wanted.teamV.type.SearchByType;
import com.wanted.teamV.type.SnsType;
import com.wanted.teamV.type.StatisticsTimeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface PostRepositoryCustom {

    Page<Post> filterPosts(List<Long> postIds, SnsType type, SearchByType searchBy, String search, Pageable pageable);
    Map<String, Long> countsByHashtagGroupByTimeType(String hashtag, LocalDateTime startDateTime, LocalDateTime endDateTime, StatisticsTimeType timeType);

}