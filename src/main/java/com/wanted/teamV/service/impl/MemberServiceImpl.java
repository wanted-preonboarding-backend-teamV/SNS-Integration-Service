package com.wanted.teamV.service.impl;

import com.wanted.teamV.dto.req.MemberApproveReqDto;
import com.wanted.teamV.dto.req.MemberJoinReqDto;
import com.wanted.teamV.dto.req.MemberLoginReqDto;
import com.wanted.teamV.dto.res.MemberCodeResDto;
import com.wanted.teamV.dto.res.MemberTokenResDto;
import com.wanted.teamV.entity.Member;
import com.wanted.teamV.exception.CustomException;
import com.wanted.teamV.repository.MemberRepository;
import com.wanted.teamV.service.MemberService;
import com.wanted.teamV.type.MemberStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.wanted.teamV.exception.ErrorCode.INVALID_REQUEST;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationCodeGenerator authenticationCodeGenerator;
    private final AuthTokenCreator authTokenCreator;

    @Override
    public MemberCodeResDto join(MemberJoinReqDto memberJoinReqDto) {
        validateUniqueAccount(memberJoinReqDto.account());

        String encryptedPassword = passwordEncoder.encode(memberJoinReqDto.password());
        String code = authenticationCodeGenerator.generate();

        Member member = Member.builder()
                .account(memberJoinReqDto.account())
                .password(encryptedPassword)
                .email(memberJoinReqDto.email())
                .code(code)
                .status(MemberStatus.WAITING)
                .build();
        memberRepository.save(member);

        return new MemberCodeResDto(code);
    }

    private void validateUniqueAccount(String account) {
        if(memberRepository.existsByAccount(account)) {
            throw new CustomException(INVALID_REQUEST);
        }
    }

    @Override
    public void approve(MemberApproveReqDto memberApproveReqDto) {
        Member member = memberRepository.getByAccount(memberApproveReqDto.account());

        if(!passwordEncoder.matches(memberApproveReqDto.password(), member.getPassword())) {
            throw new CustomException(INVALID_REQUEST);
        }

        member.verifyCode(memberApproveReqDto.code());
        member.approve();
    }

    @Override
    public MemberTokenResDto login(MemberLoginReqDto memberLoginReqDto) {
        Member member = memberRepository.getByAccount(memberLoginReqDto.account());

        if(!passwordEncoder.matches(memberLoginReqDto.password(), member.getPassword())) {
            throw new CustomException(INVALID_REQUEST);
        }

        String accessToken = authTokenCreator.createAuthToken(member.getId());
        return new MemberTokenResDto(accessToken);
    }

    @Override
    public Long extractUserId(String accessToken) {
        return authTokenCreator.extractPayload(accessToken);
    }
}
