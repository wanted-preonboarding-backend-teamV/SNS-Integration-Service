package com.wanted.teamV.controller;

import com.wanted.teamV.dto.LoginMember;
import com.wanted.teamV.dto.req.PostCreateReqDto;
import com.wanted.teamV.dto.res.PostDetailResDto;
import com.wanted.teamV.dto.res.PostResDto;
import com.wanted.teamV.entity.Member;
import com.wanted.teamV.exception.CustomException;
import com.wanted.teamV.repository.MemberRepository;
import com.wanted.teamV.service.PostService;
import com.wanted.teamV.type.SearchByType;
import com.wanted.teamV.type.SnsType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    @GetMapping
    public ResponseEntity<Page<PostResDto>> getPosts(
            @AuthenticationPrincipal LoginMember loginMember,
            @RequestParam(value = "hashtag", required = false) String hashtag,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "orderBy", defaultValue = "createdAt") String orderBy,
            @RequestParam(value = "sortBy", defaultValue = "desc") String sortBy,
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

        if (pageCount <= 0 || page < 0) {
            throw new CustomException(INVALID_PAGE_REQUEST);
        }

        Sort.Direction direction = sortBy.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, orderBy);
        PageRequest pageRequest = PageRequest.of(page, pageCount, sort);

        Page<PostResDto> paginatedPosts = postService.getPosts(hashtag, SnsType.parse(type), SearchByType.parse(searchBy), search, pageRequest);

        return ResponseEntity.ok().body(paginatedPosts);
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

