package com.github.saphyra.apphub.service.calendar.domain.event.service;

import com.github.saphyra.apphub.service.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.OccurrenceDao;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class DeleteEventService {
    private final EventDao eventDao;
    private final EventLabelMappingDao eventLabelMappingDao;
    private final OccurrenceDao occurrenceDao;

    @Transactional
    public void delete(UUID userId, UUID eventId) {
        eventDao.deleteByUserIdAndEventId(userId, eventId);
        eventLabelMappingDao.deleteByUserIdAndEventId(userId, eventId);
        occurrenceDao.deleteByUserIdAndEventId(userId, eventId);
    }
}
