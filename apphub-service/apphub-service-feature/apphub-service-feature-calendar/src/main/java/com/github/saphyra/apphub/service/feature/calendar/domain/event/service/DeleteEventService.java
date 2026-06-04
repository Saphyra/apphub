package com.github.saphyra.apphub.service.feature.calendar.domain.event.service;

import com.github.saphyra.apphub.service.feature.calendar.common.dao.CommonCalendarDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteEventService {
    private final CommonCalendarDao commonCalendarDao;

    public void delete(UUID userId, UUID eventId) {
        delete(userId, List.of(eventId));
    }

    public void delete(UUID userId, List<UUID> deletedEventIds) {
        commonCalendarDao.deleteEvents(userId, deletedEventIds);
    }
}
