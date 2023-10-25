package com.wanted.teamV.type;

import java.time.format.DateTimeFormatter;

public enum StatisticsTimeType {
    DATE(DateTimeFormatter.ISO_LOCAL_DATE),
    HOUR(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    ;

    private final DateTimeFormatter formatter;

    StatisticsTimeType(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    public DateTimeFormatter getFormatter() {
        return formatter;
    }

    public static StatisticsTimeType parse(String value) {
        for (StatisticsTimeType type : StatisticsTimeType.values()) {
            if (type.name().equals(value)) {
                return type;
            }
        }
        return DATE;
    }

}
