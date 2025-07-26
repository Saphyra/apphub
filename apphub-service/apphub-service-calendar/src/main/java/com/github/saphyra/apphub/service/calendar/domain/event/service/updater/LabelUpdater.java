package com.github.saphyra.apphub.service.calendar.domain.event.service.updater;

import com.github.saphyra.apphub.api.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.calendar.domain.event.service.EventFieldUpdater;
import com.github.saphyra.apphub.service.calendar.common.context.UpdateEventContext;
import com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.service.EventLabelMappingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class LabelUpdater implements EventFieldUpdater {
    private final EventLabelMappingService eventLabelMappingService;

    @Override
    public Object getRequestField(EventRequest request) {
        return request.getLabels();
    }

    @Override
    public Object getEventField(Event event) {
        return eventLabelMappingService.getLabelIds(event.getEventId());
    }

    @Override
    public void doUpdate(UpdateEventContext context, EventRequest request, Event event) {
        log.info("Updating labels of event {}", event.getEventId());
        eventLabelMappingService.setLabels(event.getUserId(), event.getEventId(), request.getLabels());
    }
}
