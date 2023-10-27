package com.wanted.teamV.entity;

import com.wanted.teamV.type.SnsType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Post {
    @Id
    @Column(name = "post_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String contentId;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    SnsType type;

    @Column(nullable = false)
    String title;

    @Column(nullable = false)
    String content;

    @Column(nullable = false)
    int viewCount;

    @Column(nullable = false)
    int likeCount;

    @Column(nullable = false)
    int shareCount;

    @Column(nullable = false)
    @CreatedDate
    LocalDateTime createdAt;

    @Column(nullable = false)
    @LastModifiedDate
    LocalDateTime updatedAt;

    @OneToMany(mappedBy = "post")
    List<PostHashtag> postHashtags = new ArrayList<>();

    @Builder
    public Post(String contentId, String title, String content, SnsType type, int viewCount, int likeCount, int shareCount) {
        this.contentId = contentId;
        this.title = title;
        this.content = content;
        this.type = type;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.shareCount = shareCount;
    }

}
