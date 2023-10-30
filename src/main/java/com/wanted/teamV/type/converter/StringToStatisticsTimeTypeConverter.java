package com.wanted.teamV.type.converter;

import com.wanted.teamV.type.StatisticsTimeType;
import org.springframework.core.convert.converter.Converter;

public class StringToStatisticsTimeTypeConverter implements Converter<String, StatisticsTimeType> {
    @Override
    public StatisticsTimeType convert(String value) {
        return StatisticsTimeType.parse(value.toUpperCase());
    }
}
