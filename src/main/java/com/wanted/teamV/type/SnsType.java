package com.wanted.teamV.type;

public enum SnsType {
    FACEBOOK,
    INSTAGRAM,
    X,
    THREADS
    ;

    public static SnsType parse(String value) {
        for (SnsType type : SnsType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }

}
