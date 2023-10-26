package com.wanted.teamV.entity;

import com.wanted.teamV.exception.CustomException;
import com.wanted.teamV.type.MemberStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.wanted.teamV.exception.ErrorCode.INVALID_AUTHENTICATION_CODE;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String account;

    @Column(nullable = false)
    String password;

    @Column(nullable = false)
    String email;

    String code;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    MemberStatus status;

    @Builder
    public Member(String account, String password, String email, String code, MemberStatus status) {
        this.account = account;
        this.password = password;
        this.email = email;
        this.code = code;
        this.status = status;
    }

    public void verifyCode(String code) {
         if(!this.code.equalsIgnoreCase(code)) {
            throw new CustomException(INVALID_AUTHENTICATION_CODE);
         };
    }

    public void approve() {
        this.status = MemberStatus.APPROVE;
    }
}
