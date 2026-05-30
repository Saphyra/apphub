package com.github.saphyra.apphub.service.feature.calendar.common.context;

import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.OccurrenceDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service.RecreateOccurrenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateEventContextFactory {
    private final EventDao eventDao;
    private final OccurrenceDao occurrenceDao;
    private final RecreateOccurrenceService recreateOccurrenceService;

    public UpdateEventContext create(Event event) {
        return UpdateEventContext.builder()
            .event(event)
            .eventDao(eventDao)
            .occurrenceDao(occurrenceDao)
            .recreateOccurrenceService(recreateOccurrenceService)
            .build();
    }
}
