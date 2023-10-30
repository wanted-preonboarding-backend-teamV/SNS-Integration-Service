package com.wanted.teamV.repository;

import com.wanted.teamV.entity.Post;
import com.wanted.teamV.entity.PostHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostHistoryRepository extends JpaRepository<PostHistory, Long>, PostHistoryRepositoryCustom {

}

