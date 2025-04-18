package com.github.saphyra.apphub.service.platform.scheduler;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class SchedulerProperties {
    @Value("${initialDelay}")
    private Long initialDelay;
}
