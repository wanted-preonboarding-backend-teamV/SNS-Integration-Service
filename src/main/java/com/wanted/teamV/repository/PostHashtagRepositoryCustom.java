package com.wanted.teamV.repository;

import java.util.List;

public interface PostHashtagRepositoryCustom {

    List<Long> findPostIdsByHashtag(String hashtag);

}

