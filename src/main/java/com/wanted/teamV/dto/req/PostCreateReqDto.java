package com.wanted.teamV.dto.req;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class PostCreateReqDto {

    private String contentId;
    private String title;
    private String content;
    private String type;
    private int viewCount;
    private int likeCount;
    private int shareCount;
    private List<String> hashtags;

}
