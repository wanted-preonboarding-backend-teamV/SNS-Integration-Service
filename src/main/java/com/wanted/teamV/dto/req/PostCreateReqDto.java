package com.wanted.teamV.dto.req;

import com.wanted.teamV.type.SnsType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PostCreateReqDto {

    private String contentId;
    private String title;
    private String content;
    private SnsType type;
    private int viewCount;
    private int likeCount;
    private int shareCount;
    private List<String> hashtags;

}
