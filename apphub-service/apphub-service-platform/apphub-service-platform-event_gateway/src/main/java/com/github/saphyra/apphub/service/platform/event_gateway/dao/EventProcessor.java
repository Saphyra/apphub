package com.github.saphyra.apphub.service.platform.event_gateway.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class EventProcessor {
    @NonNull
    private final UUID eventProcessorId;

    @NonNull
    private final String serviceName;

    @NonNull
    private final String url;

    @NonNull
    private final String eventName;

    @NonNull
    private OffsetDateTime lastAccess;
}
