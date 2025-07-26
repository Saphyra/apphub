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

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class EveryXDaysOccurrenceCreator implements OccurrenceCreator {
    private final OccurrenceDao occurrenceDao;
    private final OccurrenceFactory occurrenceFactory;

    @Override
    public RepetitionType getRepetitionType() {
        return null;
    }

    @Override
    public void createOccurrences(UUID userId, UUID eventId, EventRequest request) {
        int days = Integer.parseInt(request.getRepetitionData().toString());

        LocalDate date = request.getStartDate();
        LocalDate lastDate = date.minusDays(days);
        while (!date.isAfter(request.getEndDate())) {
            if (ChronoUnit.DAYS.between(date, lastDate) % days == 0) {
                Occurrence occurrence = occurrenceFactory.create(
                    userId,
                    eventId,
                    date,
                    request.getTime(),
                    request.getRemindMeBeforeDays()
                );

                occurrenceDao.save(occurrence);
            }

            date = date.plusDays(days);
        }
    }
}
