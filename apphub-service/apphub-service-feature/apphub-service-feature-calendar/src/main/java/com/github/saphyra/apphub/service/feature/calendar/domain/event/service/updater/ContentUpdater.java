package com.github.saphyra.apphub.service.feature.calendar.domain.event.service.updater;

import com.github.saphyra.apphub.api.feature.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao.DeprecatedEvent;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.service.EventFieldUpdater;
import com.github.saphyra.apphub.service.feature.calendar.common.context.UpdateEventContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class ContentUpdater implements EventFieldUpdater {
    @Override
    public Object getRequestField(EventRequest request) {
        return request.getContent();
    }

    @Override
    public Object getEventField(DeprecatedEvent event) {
        return event.getContent();
    }

    @Override
    public void doUpdate(UpdateEventContext context, EventRequest request, DeprecatedEvent event) {
        log.info("Updating content of DeprecatedEvent {}", event.getEventId());

        event.setContent(request.getContent());
    }
}
