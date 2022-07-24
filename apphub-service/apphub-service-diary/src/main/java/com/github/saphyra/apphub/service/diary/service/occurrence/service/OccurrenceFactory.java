package com.github.saphyra.apphub.service.diary.service.occurrence.service;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.diary.dao.event.Event;
import com.github.saphyra.apphub.service.diary.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.diary.dao.occurance.OccurrenceStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class OccurrenceFactory {
    private final IdGenerator idGenerator;

    public Occurrence createPending(Event event) {
        return create(event, event.getStartDate(), OccurrenceStatus.PENDING);
    }

    public Occurrence createVirtual(LocalDate date, Event event) {
        return create(event, date, OccurrenceStatus.VIRTUAL);
    }

    private Occurrence create(Event event, LocalDate date, OccurrenceStatus status) {
        return Occurrence.builder()
            .occurrenceId(idGenerator.randomUuid())
            .eventId(event.getEventId())
            .userId(event.getUserId())
            .date(date)
            .status(status)
            .build();
    }
}
