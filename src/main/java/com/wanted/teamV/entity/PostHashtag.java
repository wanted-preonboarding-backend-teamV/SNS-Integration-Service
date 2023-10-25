package com.wanted.teamV.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostHashtag {
    @Id
    @Column(name = "post_hastag_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(nullable = false)
    @JoinColumn(name = "post_id")
    Post post;

    @Column(nullable = false)
    String hashtag;

    @Builder
    public PostHashtag(Post post, String hashtag) {
        this.post = post;
        this.hashtag = hashtag;
    }
}
