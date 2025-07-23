package com.github.saphyra.apphub.service.calendar.domain.occurrence.dao;

import com.github.saphyra.apphub.api.calendar.model.OccurrenceStatus;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class OccurrenceFactory {
    private final IdGenerator idGenerator;

    public Occurrence create(UUID userId, UUID eventId, LocalDate date, LocalTime time, Integer remindMeBeforeDays) {
        return create(userId, eventId, date, time, remindMeBeforeDays, "");
    }

    public Occurrence create(UUID userId, UUID eventId, LocalDate date, LocalTime time, Integer remindMeBeforeDays, String note) {
        return Occurrence.builder()
            .occurrenceId(idGenerator.randomUuid())
            .userId(userId)
            .eventId(eventId)
            .status(OccurrenceStatus.PENDING)
            .date(date)
            .time(time)
            .note(note)
            .remindMeBeforeDays(remindMeBeforeDays)
            .reminded(false)
            .build();
    }
}
