package com.wanted.teamV.type;

public enum StatisticsValueType {
    COUNT,
    VIEW_COUNT,
    LIKE_COUNT,
    SHARE_COUNT
    ;

    public static StatisticsValueType parse(String value) {
        for (StatisticsValueType type : StatisticsValueType.values()) {
            if (type.name().equals(value)) {
                return type;
            }
        }
        return COUNT;
    }

}
