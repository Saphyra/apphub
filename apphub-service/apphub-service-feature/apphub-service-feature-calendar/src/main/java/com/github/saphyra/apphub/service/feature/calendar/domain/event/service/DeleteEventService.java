package com.github.saphyra.apphub.service.feature.calendar.domain.event.service;

import com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao.DeprecatedEventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.deprecated_dao.DeprecatedEventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.deprecated_dao.DeprecatedOccurrenceDao;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteEventService {
    private final DeprecatedEventDao eventDao;
    private final DeprecatedEventLabelMappingDao eventLabelMappingDao;
    private final DeprecatedOccurrenceDao occurrenceDao;

    @Transactional
    public void delete(UUID userId, UUID eventId) {
        eventDao.deleteByUserIdAndEventId(userId, eventId);
        eventLabelMappingDao.deleteByUserIdAndEventId(userId, eventId);
        occurrenceDao.deleteByUserIdAndEventId(userId, eventId);
    }
}
