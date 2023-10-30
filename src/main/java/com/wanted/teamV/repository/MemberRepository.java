package com.wanted.teamV.repository;

import com.wanted.teamV.entity.Member;
import com.wanted.teamV.exception.CustomException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import static com.wanted.teamV.exception.ErrorCode.NOT_FOUND;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByAccount(String account);
    Optional<Member> findByAccount(String account);

    default Member getByAccount(String account) {
        return findByAccount(account)
                .orElseThrow(() -> new CustomException(NOT_FOUND));
    }
}

