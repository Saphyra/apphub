package com.github.saphyra.apphub.service.feature.task_manager.domain;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(value = "task-manager")
@Data
public class TaskManagerProperties {
    private Duration notificationExpiration;
}
