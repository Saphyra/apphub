package com.github.saphyra.apphub.service.feature.calendar.common.context;

import com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao.DeprecatedEvent;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao.DeprecatedEventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.deprecated_dao.DeprecatedOccurrenceDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service.RecreateOccurrenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateEventContextFactory {
    private final DeprecatedEventDao eventDao;
    private final DeprecatedOccurrenceDao occurrenceDao;
    private final RecreateOccurrenceService recreateOccurrenceService;

    public UpdateEventContext create(DeprecatedEvent event) {
        return UpdateEventContext.builder()
            .event(event)
            .eventDao(eventDao)
            .occurrenceDao(occurrenceDao)
            .recreateOccurrenceService(recreateOccurrenceService)
            .build();
    }
}
