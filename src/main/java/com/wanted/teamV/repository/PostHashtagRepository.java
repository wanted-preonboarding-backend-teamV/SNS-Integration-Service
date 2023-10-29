package com.wanted.teamV.repository;

import com.wanted.teamV.entity.PostHashtag;
import com.wanted.teamV.entity.PostHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long> {
    @Query("SELECT ph.hashtag From PostHashtag ph WHERE ph.post.id = :postId")
    List<String> findHashTagsByPostId(Long postId);
}

