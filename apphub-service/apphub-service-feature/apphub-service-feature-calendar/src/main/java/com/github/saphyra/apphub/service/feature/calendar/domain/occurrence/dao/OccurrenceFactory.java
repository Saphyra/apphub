package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao;

import com.github.saphyra.apphub.api.feature.calendar.model.OccurrenceStatus;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OccurrenceFactory {
    private final IdGenerator idGenerator;
    private final DateTimeUtil dateTimeUtil;

    public Occurrence create(UUID userId, UUID eventId, LocalDate date, LocalTime time, Integer remindMeBeforeDays) {
        return create(userId, eventId, date, time, remindMeBeforeDays, "", null);
    }

    public Occurrence create(UUID userId, UUID eventId, LocalDate date, LocalTime time, Integer remindMeBeforeDays, String note, Boolean autoDone) {
        return Occurrence.builder()
            .userId(userId)
            .eventId(eventId)
            .occurrenceId(idGenerator.randomUuid())
            .status(date.isBefore(dateTimeUtil.getCurrentDate()) ? OccurrenceStatus.EXPIRED : OccurrenceStatus.PENDING)
            .date(date)
            .time(time)
            .note(note)
            .remindMeBeforeDays(remindMeBeforeDays)
            .reminded(false)
            .autoDone(autoDone)
            .build();
    }
}
