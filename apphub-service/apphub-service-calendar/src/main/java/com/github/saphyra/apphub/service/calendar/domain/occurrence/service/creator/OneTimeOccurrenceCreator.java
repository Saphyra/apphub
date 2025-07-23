package com.github.saphyra.apphub.service.calendar.domain.occurrence.service.creator;

import com.github.saphyra.apphub.api.calendar.model.RepetitionType;
import com.github.saphyra.apphub.api.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.OccurrenceDao;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.OccurrenceFactory;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.service.OccurrenceCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class OneTimeOccurrenceCreator implements OccurrenceCreator {
    private final OccurrenceFactory occurrenceFactory;
    private final OccurrenceDao occurrenceDao;

    @Override
    public RepetitionType getRepetitionType() {
        return RepetitionType.ONE_TIME;
    }

    @Override
    public void createOccurrences(UUID userId, UUID eventId, EventRequest request) {
        Occurrence occurrence = occurrenceFactory.create(userId, eventId, request.getStartDate(), request.getTime(), request.getRemindMeBeforeDays());

        occurrenceDao.save(occurrence);
    }
}
