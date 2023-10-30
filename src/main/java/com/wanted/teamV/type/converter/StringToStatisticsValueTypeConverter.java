package com.wanted.teamV.type.converter;

import com.wanted.teamV.type.StatisticsValueType;
import org.springframework.core.convert.converter.Converter;

public class StringToStatisticsValueTypeConverter implements Converter<String, StatisticsValueType> {
    @Override
    public StatisticsValueType convert(String value) {
        return StatisticsValueType.parse(value.toUpperCase());
    }
}
