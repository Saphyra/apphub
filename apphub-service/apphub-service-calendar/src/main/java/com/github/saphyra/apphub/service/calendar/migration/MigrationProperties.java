package com.github.saphyra.apphub.service.calendar.migration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Data
@Configuration
@ConfigurationProperties(prefix = "migration")
public class MigrationProperties {
    private Duration defaultEndDateOffset;
}
