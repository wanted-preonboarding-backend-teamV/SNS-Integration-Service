package com.wanted.teamV.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wanted.teamV.entity.Post;
import com.wanted.teamV.entity.PostHashtag;
import com.wanted.teamV.type.SnsType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@NoArgsConstructor
public class PostResDto {

    Long postId;
    String contentId;
    SnsType type;
    String title;
    String content;
    int viewCount;
    int likeCount;
    int shareCount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    LocalDateTime updatedAt;

    List<String> hashtags = new ArrayList<>();

    public static PostResDto mapToPostResDto(Post post) {
        PostResDto postResDto = new PostResDto();
        postResDto.setPostId(post.getId());
        postResDto.setContentId(post.getContentId());
        postResDto.setType(post.getType());
        postResDto.setTitle(post.getTitle());
        postResDto.setContent(post.getContent());
        postResDto.setViewCount(post.getViewCount());
        postResDto.setLikeCount(post.getLikeCount());
        postResDto.setShareCount(post.getShareCount());
        postResDto.setCreatedAt(post.getCreatedAt());
        postResDto.setUpdatedAt(post.getUpdatedAt());
        postResDto.setHashtags(extractHashtagsFromPost(post));

        return postResDto;
    }

    private static List<String> extractHashtagsFromPost(Post post) {
        return post.getPostHashtags()
                .stream()
                .map(PostHashtag::getHashtag)
                .collect(Collectors.toList());
    }

}
