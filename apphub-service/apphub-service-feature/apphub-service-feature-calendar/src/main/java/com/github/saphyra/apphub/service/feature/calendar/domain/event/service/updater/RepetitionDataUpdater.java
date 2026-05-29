package com.github.saphyra.apphub.service.feature.calendar.domain.event.service.updater;

import com.github.saphyra.apphub.api.feature.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.service.feature.calendar.common.context.UpdateEventContext;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao.DeprecatedEvent;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.service.EventFieldUpdater;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
class RepetitionDataUpdater implements EventFieldUpdater {
    private final ObjectMapper objectMapper;

    @Override
    public Object getRequestField(EventRequest request) {
        return request.getRepetitionData();
    }

    @Override
    public Object getEventField(DeprecatedEvent event) {
        return event.getRepetitionData();
    }

    @Override
    public void doUpdate(UpdateEventContext context, EventRequest request, DeprecatedEvent event) {
        log.info("Updating repetitionData of DeprecatedEvent {}", event.getEventId());

        event.setRepetitionData(objectMapper.writeValueAsString(request.getRepetitionData()));

        context.occurrenceRecreationNeeded();
    }
}
