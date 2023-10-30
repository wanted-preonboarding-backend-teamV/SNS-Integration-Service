package com.wanted.teamV.type;

public enum StatisticsSortType {
    ASC,
    DESC;

    public static StatisticsSortType parse(String value) {
        for (StatisticsSortType type : StatisticsSortType.values()) {
            if (type.name().equals(value)) {
                return type;
            }
        }
        return DESC;
    }
}
