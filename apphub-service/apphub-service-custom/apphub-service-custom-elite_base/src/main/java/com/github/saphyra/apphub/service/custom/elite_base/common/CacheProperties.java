package com.github.saphyra.apphub.service.custom.elite_base.common;

import lombok.Data;

import java.time.Duration;

@Data
public class CacheProperties {
    private Duration expireAfterAccess;
    private Duration bufferSynchronizationInterval;
    private Duration bufferSynchronizationCheckInterval;
    private int maxBufferSize;
    private Duration cacheReadTimeout;
    private Duration loadRetryDelay;
}
