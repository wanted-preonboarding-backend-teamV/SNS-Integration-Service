package com.wanted.teamV.dto.res;

import com.wanted.teamV.type.StatisticsTimeType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class StatisticsResDto {
    private String time;
    private long value;

    public StatisticsResDto(StatisticsTimeType timeType, LocalDateTime time, long value) {
        this.time = time.format(timeType.getFormatter());
        this.value = value;
    }
}
