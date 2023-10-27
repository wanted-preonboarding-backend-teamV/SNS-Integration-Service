package com.wanted.teamV.controller;

import com.wanted.teamV.dto.req.PostCreateReqDto;
import com.wanted.teamV.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    // 게시물 생성 API
    @PostMapping
    public ResponseEntity<Void> createPost(
            @RequestBody PostCreateReqDto request
    ) {
        postService.createPost(request);
        return ResponseEntity.ok().build();
    }

}
