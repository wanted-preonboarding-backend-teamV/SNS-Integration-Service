package com.wanted.teamV.controller;

import com.wanted.teamV.dto.LoginMember;
import com.wanted.teamV.dto.req.PostCreateReqDto;
import com.wanted.teamV.dto.res.ListResDto;
import com.wanted.teamV.dto.res.PostDetailResDto;
import com.wanted.teamV.dto.res.PostResDto;
import com.wanted.teamV.entity.Member;
import com.wanted.teamV.exception.CustomException;
import com.wanted.teamV.repository.MemberRepository;
import com.wanted.teamV.service.PostService;
import com.wanted.teamV.type.OrderByType;
import com.wanted.teamV.type.SearchByType;
import com.wanted.teamV.type.SnsType;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.wanted.teamV.exception.ErrorCode.*;

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

    // 게시물 목록 조회 API
    @GetMapping
    public ResponseEntity<ListResDto<PostResDto>> getPosts(
            @AuthenticationPrincipal LoginMember loginMember,
            @RequestParam(value = "hashtag", required = false) String hashtag,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "orderBy", defaultValue = "created_at_desc") String orderBy,
            @RequestParam(value = "searchBy", defaultValue = "both") String searchBy,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "pageCount", defaultValue = "10") int pageCount,
            @RequestParam(value = "page", defaultValue = "0") int page
    ) {
        if (type != null && SnsType.parse(type) == null) {
            throw new CustomException(INVALID_SNS_TYPE);
        }

        if (hashtag == null || hashtag.isBlank()) {
            hashtag = getMemberAccount(loginMember.id());
        }

        ListResDto<PostResDto> responses = postService.getPosts(hashtag, SnsType.parse(type),
                OrderByType.parse(orderBy), SearchByType.parse(searchBy), search, pageCount, page);
        return ResponseEntity.ok(responses);
    }

    //게시물 상세 조회 API
    @GetMapping("/post/{postId}")
    public ResponseEntity<PostDetailResDto> getPostDetails(
            @AuthenticationPrincipal LoginMember loginMember,
            @PathVariable Long postId
    ) {
        PostDetailResDto responseDto = postService.getPostDetail(postId, loginMember.id());
        return ResponseEntity.ok(responseDto);
    }

    //게시물 좋아요 API
    @PostMapping("/likes/{postId}")
    public ResponseEntity<?> increaseLike(
            @AuthenticationPrincipal LoginMember loginMember,
            @PathVariable Long postId
    ) {
        ResponseEntity<?> response = postService.increaseLike(postId, loginMember.id());
        return ResponseEntity.ok(response.getStatusCode());
    }

    @PostMapping("/shares/{postId}")
    public ResponseEntity<?> increaseShare(
            @AuthenticationPrincipal LoginMember loginMember,
            @PathVariable Long postId
    ) {
        ResponseEntity<?> response = postService.increaseShare(postId, loginMember.id());
        return ResponseEntity.ok(response.getStatusCode());
    }

    private String getMemberAccount(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(INVALID_REQUEST));
        return member.getAccount();
    }
}

