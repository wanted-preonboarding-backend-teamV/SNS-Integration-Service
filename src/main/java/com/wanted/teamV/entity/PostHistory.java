package com.wanted.teamV.entity;

import com.wanted.teamV.type.HistoryType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostHistory {
    @Id
    @Column(name = "history_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(nullable = false)
    @JoinColumn(name = "post_id")
    Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(nullable = false)
    @JoinColumn(name = "member_id")
    Member member;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    HistoryType type;

    @Column(nullable = false)
    @CreatedDate
    LocalDateTime createdAt;

    @Builder
    public PostHistory(Post post, Member member, HistoryType type) {
        this.post = post;
        this.member = member;
        this.type = type;
    }
}
