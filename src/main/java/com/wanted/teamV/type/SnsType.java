package com.wanted.teamV.type;

import com.wanted.teamV.exception.CustomException;

import java.util.Arrays;

import static com.wanted.teamV.exception.ErrorCode.INVALID_REQUEST;

public enum SnsType {
    FACEBOOK,
    INSTAGRAM,
    X,
    THREADS
    ;

    public static SnsType toEnum(String value) {
        return Arrays.stream(values())
                .filter(type -> type.isSameType(value))
                .findFirst()
                .orElseThrow(() -> new CustomException(INVALID_REQUEST));
    }

    private boolean isSameType(String type) {
        return this.name().equalsIgnoreCase(type);
    }
}
