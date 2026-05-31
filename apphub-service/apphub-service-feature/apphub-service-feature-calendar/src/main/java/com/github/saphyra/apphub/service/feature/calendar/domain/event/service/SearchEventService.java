package com.github.saphyra.apphub.service.feature.calendar.domain.event.service;

import com.github.saphyra.apphub.api.feature.calendar.model.response.EventResponse;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.OccurrenceDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class SearchEventService {
    private final EventDao eventDao;
    private final OccurrenceDao occurrenceDao;
    private final EventResponseMapper eventResponseMapper;

    public List<EventResponse> search(UUID userId, String search) {
        ValidationUtil.minLength(search, 3, "searchText");

        List<Event> events =  eventDao.getByUserId(userId)
            .stream()
            .filter(event -> eventMatches(event, search) || occurrenceMatches(userId, event.getEventId(), search))
            .toList();

        return eventResponseMapper.toResponse(userId, events);
    }

    private boolean occurrenceMatches(UUID userId,  UUID eventId, String search) {
        return occurrenceDao.getByEventId(userId, eventId)
            .stream()
            .anyMatch(occurrence -> occurrenceMatches(occurrence, search));
    }

    private boolean occurrenceMatches(Occurrence occurrence, String search) {
        return matches(search, occurrence.getNote());
    }

    private boolean eventMatches(Event event, String search) {
        return matches(search, event.getTitle()) || matches(search, event.getContent());
    }

    private static boolean matches(String search, String value) {
        return value.toLowerCase().contains(search.toLowerCase());
    }
}
