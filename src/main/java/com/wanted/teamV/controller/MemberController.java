package com.wanted.teamV.controller;

import com.wanted.teamV.dto.LoginMember;
import com.wanted.teamV.dto.req.MemberApproveReqDto;
import com.wanted.teamV.dto.req.MemberJoinReqDto;
import com.wanted.teamV.dto.req.MemberLoginReqDto;
import com.wanted.teamV.dto.res.MemberCodeResDto;
import com.wanted.teamV.dto.res.MemberTokenResDto;
import com.wanted.teamV.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<MemberCodeResDto> join(
            @Valid @RequestBody MemberJoinReqDto memberJoinReqDto
    ) {
        MemberCodeResDto memberCodeResDto = memberService.join(memberJoinReqDto);
        return ResponseEntity.ok(memberCodeResDto);
    }

    @PostMapping("/approve")
    public ResponseEntity<Void> approve(
            @RequestBody MemberApproveReqDto memberApproveReqDto
    ) {
        memberService.approve(memberApproveReqDto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<MemberTokenResDto> login(
            @RequestBody MemberLoginReqDto memberLoginReqDto
    ) {
        MemberTokenResDto memberTokenResDto = memberService.login(memberLoginReqDto);
        return ResponseEntity.ok(memberTokenResDto);
    }

    //JWT 유효성을 검증하는 예시입니다.
    @GetMapping("/validate/token")
    public ResponseEntity<Void> validateToken(
            @AuthenticationPrincipal LoginMember loginMember
    ) {
        return ResponseEntity.ok().build();
    }
}
