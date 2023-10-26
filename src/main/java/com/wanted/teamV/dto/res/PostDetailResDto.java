package com.wanted.teamV.dto.res;

import com.wanted.teamV.entity.PostHashtag;
import com.wanted.teamV.type.SnsType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PostDetailResDto {
    private Long id;
    private String contentId;
    private SnsType type;
    private String title;
    private String content;
    private int viewCount;
    private int likeCount;
    private int shareCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<PostHashtag> postHashtags;
}
