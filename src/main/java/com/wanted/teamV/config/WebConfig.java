package com.wanted.teamV.config;

import com.wanted.teamV.type.converter.StringToStatisticsSortTypeConverter;
import com.wanted.teamV.type.converter.StringToStatisticsTimeTypeConverter;
import com.wanted.teamV.type.converter.StringToStatisticsValueTypeConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToStatisticsValueTypeConverter());
        registry.addConverter(new StringToStatisticsTimeTypeConverter());
        registry.addConverter(new StringToStatisticsSortTypeConverter());
    }
}
