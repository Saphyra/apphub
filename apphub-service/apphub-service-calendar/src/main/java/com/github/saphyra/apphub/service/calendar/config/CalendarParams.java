package com.github.saphyra.apphub.service.calendar.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@Data
@ConfigurationProperties(prefix = "calendar")
public class CalendarParams {
    private Duration maxEventDuration;
}
