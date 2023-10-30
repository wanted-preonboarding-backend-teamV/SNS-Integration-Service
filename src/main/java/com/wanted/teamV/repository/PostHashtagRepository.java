package com.wanted.teamV.repository;

import com.wanted.teamV.entity.PostHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long>, PostHashtagRepositoryCustom {

    List<PostHashtag> findByPostId(Long postId);

    List<PostHashtag> findAllByHashtag(String hashtag);
}

