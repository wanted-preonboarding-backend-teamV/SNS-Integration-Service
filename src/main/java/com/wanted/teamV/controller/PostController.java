package com.wanted.teamV.controller;

import com.wanted.teamV.dto.LoginMember;
import com.wanted.teamV.dto.req.PostCreateReqDto;
import com.wanted.teamV.entity.Member;
import com.wanted.teamV.exception.CustomException;
import com.wanted.teamV.repository.MemberRepository;
import com.wanted.teamV.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.wanted.teamV.exception.ErrorCode.INVALID_REQUEST;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final MemberRepository memberRepository;

    // 게시물 생성 API
    @PostMapping
    public ResponseEntity<Void> createPost(
            @AuthenticationPrincipal LoginMember loginMember,
            @RequestBody PostCreateReqDto request
    ) {
        List<String> hashtags = request.getHashtags();
        String account = getMemberAccount(loginMember.id());
        hashtags.add(account);

        request.setHashtags(hashtags);

        postService.createPost(request);
        return ResponseEntity.ok().build();
    }

    private String getMemberAccount(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(INVALID_REQUEST));
        return member.getAccount();
    }

}
