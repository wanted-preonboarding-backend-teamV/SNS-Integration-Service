package com.wanted.teamV.repository;

import com.wanted.teamV.entity.Post;
import com.wanted.teamV.type.OrderByType;
import com.wanted.teamV.type.SearchByType;
import com.wanted.teamV.type.SnsType;

import com.wanted.teamV.type.StatisticsTimeType;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;

public interface PostRepositoryCustom {

    List<Post> filterPosts(List<Long> postIds, SnsType type, OrderByType orderBy, SearchByType searchBy, String search);
    Map<String, Long> countsByHashtagGroupByTimeType(String hashtag, LocalDateTime startDateTime, LocalDateTime endDateTime, StatisticsTimeType timeType);

}