package com.github.saphyra.apphub.service.diary.service.occurrence.service;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.diary.dao.event.Event;
import com.github.saphyra.apphub.service.diary.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.diary.dao.occurance.OccurrenceStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class OccurrenceFactory {
    private final IdGenerator idGenerator;

    public Occurrence create(Event event) {
        return Occurrence.builder()
            .occurrenceId(idGenerator.randomUuid())
            .eventId(event.getEventId())
            .userId(event.getUserId())
            .date(event.getStartDate())
            .status(OccurrenceStatus.PENDING)
            .build();
    }
}
