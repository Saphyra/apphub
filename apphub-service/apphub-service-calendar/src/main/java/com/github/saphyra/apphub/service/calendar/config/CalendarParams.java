package com.github.saphyra.apphub.service.calendar.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "calendar")
public class CalendarParams {
    private Integer maxEventDurationDays;
}
