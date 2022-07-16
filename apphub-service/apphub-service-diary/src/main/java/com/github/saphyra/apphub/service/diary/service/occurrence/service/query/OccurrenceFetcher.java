package com.github.saphyra.apphub.service.diary.service.occurrence.service.query;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalMap;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.diary.dao.event.Event;
import com.github.saphyra.apphub.service.diary.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.diary.dao.occurance.OccurrenceDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
@Slf4j
@Component
//TODO unit test
class OccurrenceFetcher {
    private final OccurrenceDao occurrenceDao;
    private final OneTimeEventHandler oneTimeEventHandler;
    private final DaysOfWeekEventHandler daysOfWeekEventHandler;
    private final EveryXDayEventHandler everyXDayEventHandler;

    List<Occurrence> fetchOccurrenceOfEvent(Event event, List<LocalDate> dates) {
        log.debug("Fetching Occurrences for event {}", event);
        List<Occurrence> occurrences = occurrenceDao.getByEventId(event.getEventId());
        log.debug("Occurrences related to event: {}", occurrences);

        OptionalMap<LocalDate, Occurrence> occurrenceMapping = CollectionUtils.mapToOptionalMap(occurrences, Occurrence::getDate, Function.identity());

        switch (event.getRepetitionType()) {
            case ONE_TIME:
                return oneTimeEventHandler.handleOneTimeEvent(occurrences, dates);
            case DAYS_OF_WEEK:
                return daysOfWeekEventHandler.handleDaysOfWeekEvent(event, dates, occurrenceMapping);
            case EVERY_X_DAYS:
                return everyXDayEventHandler.handleEveryXDayEvent(event, dates, occurrences, occurrenceMapping);
            default:
                throw ExceptionFactory.reportedException(HttpStatus.NOT_IMPLEMENTED, ErrorCode.GENERAL_ERROR, "Unhandled repetitionType: " + event.getRepetitionType());
        }
    }
}
