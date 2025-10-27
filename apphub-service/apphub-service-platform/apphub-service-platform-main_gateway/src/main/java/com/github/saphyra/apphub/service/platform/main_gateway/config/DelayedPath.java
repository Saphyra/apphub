package com.github.saphyra.apphub.service.platform.main_gateway.config;

import lombok.Data;

import java.time.Duration;

@Data
public class DelayedPath {
    private String path;
    private boolean enabled;
    private Duration minDelay;
    private Duration maxDelay;
}
