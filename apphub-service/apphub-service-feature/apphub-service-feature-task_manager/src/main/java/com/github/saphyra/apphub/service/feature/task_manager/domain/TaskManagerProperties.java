package com.github.saphyra.apphub.service.feature.task_manager.domain;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConfigurationProperties(value = "task-manager")
@Data
public class TaskManagerProperties {
    private Duration notificationExpiration;
}
