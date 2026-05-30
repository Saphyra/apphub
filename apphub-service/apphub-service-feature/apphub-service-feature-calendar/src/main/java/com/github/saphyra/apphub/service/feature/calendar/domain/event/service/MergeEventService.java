package com.github.saphyra.apphub.service.feature.calendar.domain.event.service;

import com.github.saphyra.apphub.api.feature.calendar.model.RepetitionType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.CommonUtils;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.OccurrenceDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class MergeEventService {
    private final EventDao eventDao;
    private final OccurrenceDao occurrenceDao;
    private final DeleteEventService deleteEventService;

    public void merge(UUID userId, UUID eventId) {
        Event parent = eventDao.findByIdValidated(userId, eventId);

        if (parent.getRepetitionType() != RepetitionType.ONE_TIME) {
            throw ExceptionFactory.invalidParam("eventId", "invalid type");
        }

        List<BiWrapper<UUID, UUID>> occurrencesToDelete = new ArrayList<>(); //List<EventId, OccurrenceId>
        List<Occurrence> modifiedOccurrences = new ArrayList<>();
        List<UUID> deletedEventIds = new ArrayList<>();
        eventDao.getByUserId(parent.getUserId())
            .stream()
            .filter(event -> !event.getEventId().equals(eventId))
            .filter(event -> event.getRepetitionType() == RepetitionType.ONE_TIME)
            .filter(event -> titlesMatch(event.getTitle(), parent.getTitle()))
            .forEach(originalEvent -> merge(userId, originalEvent, parent.getEventId(), modifiedOccurrences, occurrencesToDelete, deletedEventIds));

        deleteEventService.delete(userId, deletedEventIds);
    }

    private void merge(UUID userId, Event originalEvent, UUID newEventId, List<Occurrence> modifiedOccurrences, List<BiWrapper<UUID, UUID>> occurrencesToDelete, List<UUID> deletedEventIds) {
        occurrenceDao.getByEventId(userId, originalEvent.getEventId())
            .forEach(occurrence -> merge(originalEvent, newEventId, occurrence, modifiedOccurrences, occurrencesToDelete));

        deletedEventIds.add(originalEvent.getEventId());
    }

    private void merge(Event originalEvent, UUID newEventId, Occurrence occurrence, List<Occurrence> modifiedOccurrences, List<BiWrapper<UUID, UUID>> occurrencesToDelete) {
        occurrencesToDelete.add(new BiWrapper<>(originalEvent.getEventId(), occurrence.getOccurrenceId()));

        occurrence.setEventId(newEventId);
        occurrence.setNote(assembleNote(originalEvent.getContent(), occurrence.getNote()));
        occurrence.setTime(CommonUtils.firstNotNull(occurrence.getTime(), originalEvent.getTime()));
        occurrence.setRemindMeBeforeDays(CommonUtils.firstNotNull(occurrence.getRemindMeBeforeDays(), originalEvent.getRemindMeBeforeDays()));

        modifiedOccurrences.add(occurrence);
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
