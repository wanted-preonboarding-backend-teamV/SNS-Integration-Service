package com.wanted.teamV.controller;

import com.wanted.teamV.dto.res.PostDetailResDto;
import com.wanted.teamV.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    @GetMapping("/{postId}/{memberId}")
    public ResponseEntity<PostDetailResDto> getPostDetails(
            @PathVariable Long postId, @PathVariable Long memberId
    ) {
        PostDetailResDto responseDto = postService.getPostDetail(postId, memberId);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/likes/{postId}")
    public ResponseEntity<?> increaseLike(
            @PathVariable Long postId
    ) {
        Long memberId = 1L;
        ResponseEntity<?> response = postService.increaseLike(postId, memberId);
        return ResponseEntity.ok(response.getStatusCode());
    }

    @PostMapping("/shares/{postId}")
    public ResponseEntity<?> increaseShare(
            @PathVariable Long postId
    ) {
        Long memberId = 1L;
        ResponseEntity<?> response = postService.increaseShare(postId, memberId);
        return ResponseEntity.ok(response.getStatusCode());
    }
}
