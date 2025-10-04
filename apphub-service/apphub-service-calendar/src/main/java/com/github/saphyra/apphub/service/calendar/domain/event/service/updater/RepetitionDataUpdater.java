package com.github.saphyra.apphub.service.calendar.domain.event.service.updater;

import com.github.saphyra.apphub.api.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.calendar.domain.event.service.EventFieldUpdater;
import com.github.saphyra.apphub.service.calendar.common.context.UpdateEventContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class RepetitionDataUpdater implements EventFieldUpdater {
    private final ObjectMapperWrapper objectMapperWrapper;

    @Override
    public Object getRequestField(EventRequest request) {
        return request.getRepetitionData();
    }

    @Override
    public Object getEventField(Event event) {
        return event.getRepetitionData();
    }

    @Override
    public void doUpdate(UpdateEventContext context, EventRequest request, Event event) {
        log.info("Updating repetitionData of Event {}", event.getEventId());

        event.setRepetitionData(objectMapperWrapper.writeValueAsPrettyString(request.getRepetitionData()));

        context.occurrenceRecreationNeeded();
    }
}
