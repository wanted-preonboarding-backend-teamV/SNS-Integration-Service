package com.wanted.teamV.service;

import com.wanted.teamV.dto.req.MemberApproveReqDto;
import com.wanted.teamV.dto.req.MemberJoinReqDto;
import com.wanted.teamV.dto.req.MemberLoginReqDto;
import com.wanted.teamV.dto.res.MemberCodeResDto;
import com.wanted.teamV.dto.res.MemberTokenResDto;

public interface MemberService {
    MemberCodeResDto join(MemberJoinReqDto memberJoinReqDto);
    void approve(MemberApproveReqDto memberApproveReqDto);
    MemberTokenResDto login(MemberLoginReqDto memberLoginReqDto);
    Long extractUserId(String accessToken);
}
