package com.wanted.teamV.type.converter;

import com.wanted.teamV.type.StatisticsSortType;
import org.springframework.core.convert.converter.Converter;

public class StringToStatisticsSortTypeConverter implements Converter<String, StatisticsSortType> {
    @Override
    public StatisticsSortType convert(String value) {
        return StatisticsSortType.parse(value.toUpperCase());
    }
}
