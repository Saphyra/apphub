package com.github.saphyra.apphub.service.calendar.domain.occurrence.service.creator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.saphyra.apphub.api.calendar.model.RepetitionType;
import com.github.saphyra.apphub.api.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.OccurrenceDao;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.OccurrenceFactory;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.service.OccurrenceCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
// TODO unit test
class DaysOfMonthOccurrenceCreator implements OccurrenceCreator {
    private final OccurrenceDao occurrenceDao;
    private final OccurrenceFactory occurrenceFactory;
    private final ObjectMapperWrapper objectMapperWrapper;

    @Override
    public RepetitionType getRepetitionType() {
        return RepetitionType.DAYS_OF_MONTH;
    }

    @Override
    public void createOccurrences(UUID userId, UUID eventId, EventRequest request) {
        TypeReference<Set<Integer>> typeReference = new TypeReference<>() {
        };
        Set<Integer> daysOfMonth = objectMapperWrapper.convertValue(request.getRepetitionData(), typeReference);

        LocalDate date = request.getStartDate();
        while (!date.isAfter(request.getEndDate())) {
            if (daysOfMonth.contains(date.getDayOfMonth())) {
                Occurrence occurrence = occurrenceFactory.create(
                    userId,
                    eventId,
                    date,
                    request.getTime(),
                    request.getRemindMeBeforeDays()
                );
                occurrenceDao.save(occurrence);
            }
            date = date.plusDays(1);
        }
    }
}
