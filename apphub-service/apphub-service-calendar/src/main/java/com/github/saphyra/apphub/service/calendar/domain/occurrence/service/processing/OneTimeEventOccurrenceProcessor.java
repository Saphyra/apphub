package com.github.saphyra.apphub.service.calendar.domain.occurrence.service.processing;

import com.github.saphyra.apphub.api.calendar.model.RepetitionType;
import com.github.saphyra.apphub.api.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.OccurrenceDao;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.OccurrenceFactory;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.service.condition.RepetitionTypeConditionSelector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Slf4j
//TODO unit test
class OneTimeEventOccurrenceProcessor extends AbstractOccurrenceProcessor {
    OneTimeEventOccurrenceProcessor(OccurrenceFactory occurrenceFactory, OccurrenceDao occurrenceDao, RepetitionTypeConditionSelector repetitionTypeConditionSelector, DateTimeUtil dateTimeUtil) {
        super(occurrenceFactory, occurrenceDao, repetitionTypeConditionSelector, dateTimeUtil);
    }

    @Override
    public RepetitionType getRepetitionType() {
        return RepetitionType.ONE_TIME;
    }

    @Override
    protected LocalDate getEndDate(EventRequest request) {
        return request.getStartDate();
    }

    @Override
    protected LocalDate getEndDate(Event event) {
        return event.getStartDate();
    }
}