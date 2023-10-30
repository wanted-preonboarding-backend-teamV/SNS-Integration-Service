package com.wanted.teamV.type;

public enum HistoryType {
    VIEW,
    LIKE,
    SHARE
    ;

    public static HistoryType parseStatisticsValueType(StatisticsValueType valueType) {
        return switch (valueType) {
            case VIEW_COUNT, COUNT -> VIEW;
            case LIKE_COUNT -> LIKE;
            case SHARE_COUNT -> SHARE;
        };
    }
}
