package com.wanted.teamV.config;

import com.wanted.teamV.controller.AuthenticationPrincipalArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import com.wanted.teamV.type.converter.StringToStatisticsSortTypeConverter;
import com.wanted.teamV.type.converter.StringToStatisticsTimeTypeConverter;
import com.wanted.teamV.type.converter.StringToStatisticsValueTypeConverter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthenticationPrincipalArgumentResolver authenticationPrincipalArgumentResolver;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToStatisticsValueTypeConverter());
        registry.addConverter(new StringToStatisticsTimeTypeConverter());
        registry.addConverter(new StringToStatisticsSortTypeConverter());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authenticationPrincipalArgumentResolver);
    }
}
