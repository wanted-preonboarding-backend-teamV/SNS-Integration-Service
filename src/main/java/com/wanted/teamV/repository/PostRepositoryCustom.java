package com.wanted.teamV.repository;

import com.wanted.teamV.entity.Post;
import com.wanted.teamV.type.OrderByType;
import com.wanted.teamV.type.SearchByType;
import com.wanted.teamV.type.SnsType;

import java.util.List;

public interface PostRepositoryCustom {

    List<Post> filterPosts(List<Long> postIds, SnsType type, OrderByType orderBy, SearchByType searchBy,
                           String search);

}

