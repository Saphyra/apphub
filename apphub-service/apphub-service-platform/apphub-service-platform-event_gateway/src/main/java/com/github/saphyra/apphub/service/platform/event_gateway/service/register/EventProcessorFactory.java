package com.github.saphyra.apphub.service.platform.event_gateway.service.register;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.OffsetDateTimeProvider;
import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class EventProcessorFactory {
    private final IdGenerator idGenerator;
    private final OffsetDateTimeProvider offsetDateTimeProvider;

    EventProcessor create(RegisterProcessorRequest request) {
        log.info("Creating new eventProcessor... {}", request);
        return EventProcessor.builder()
            .eventProcessorId(idGenerator.randomUUID())
            .serviceName(request.getServiceName())
            .url(request.getUrl())
            .eventName(request.getEventName())
            .lastAccess(offsetDateTimeProvider.getCurrentDate())
            .build();
    }
}
