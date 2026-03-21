package com.github.saphyra.apphub.service.feature.calendar.domain.event.service;

import com.github.saphyra.apphub.api.feature.calendar.model.RepetitionType;
import com.github.saphyra.apphub.lib.common_util.CommonUtils;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.OccurrenceDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class MergeEventService {
    private final EventDao eventDao;
    private final OccurrenceDao occurrenceDao;
    private final DeleteEventService deleteEventService;

    public void merge(UUID eventId) {
        Event parent = eventDao.findByIdValidated(eventId);

        if (parent.getRepetitionType() != RepetitionType.ONE_TIME) {
            throw ExceptionFactory.invalidParam("eventId", "invalid type");
        }

        eventDao.getByUserId(parent.getUserId())
            .stream()
            .filter(event -> !event.getEventId().equals(eventId))
            .filter(event -> event.getRepetitionType() == RepetitionType.ONE_TIME)
            .filter(event -> titlesMatch(event.getTitle(), parent.getTitle()))
            .forEach(event -> merge(parent, event));
    }

    private void merge(Event parent, Event event) {
        occurrenceDao.getByEventId(event.getEventId())
            .forEach(occurrence -> merge(parent, event, occurrence));
    }

    private void merge(Event parent, Event event, Occurrence occurrence) {
        occurrence.setEventId(parent.getEventId());
        occurrence.setNote(assembleNote(event.getContent(), occurrence.getNote()));
        occurrence.setTime(CommonUtils.firstNotNull(occurrence.getTime(), event.getTime()));
        occurrence.setRemindMeBeforeDays(CommonUtils.firstNotNull(occurrence.getRemindMeBeforeDays(), event.getRemindMeBeforeDays()));

        occurrenceDao.save(occurrence);
        deleteEventService.delete(event.getUserId(), event.getEventId());
    }

    private String assembleNote(String content, String note) {
        return Stream.of(content, note)
            .filter(s -> !s.isBlank())
            .collect(Collectors.joining("\n\n"));
    }

    private boolean titlesMatch(String title1, String title2) {
        return title1.trim().equalsIgnoreCase(title2.trim());
    }
}
