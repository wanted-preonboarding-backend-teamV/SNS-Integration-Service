package com.wanted.teamV.repository;

import com.wanted.teamV.entity.PostHashtag;
import com.wanted.teamV.entity.PostHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long> {

}

