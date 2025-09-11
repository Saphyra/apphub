package com.github.saphyra.apphub.service.calendar.domain.occurrence.service.processing;

import com.github.saphyra.apphub.api.calendar.model.RepetitionType;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.OccurrenceDao;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.OccurrenceFactory;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.service.condition.RepetitionTypeConditionSelector;
import org.springframework.stereotype.Component;

@Component
class DaysOfWeekOccurrenceProcessor extends AbstractOccurrenceProcessor{
    DaysOfWeekOccurrenceProcessor(OccurrenceFactory occurrenceFactory, OccurrenceDao occurrenceDao, RepetitionTypeConditionSelector repetitionTypeConditionSelector, DateTimeUtil dateTimeUtil) {
        super(occurrenceFactory, occurrenceDao, repetitionTypeConditionSelector, dateTimeUtil);
    }

    @Override
    public RepetitionType getRepetitionType() {
        return RepetitionType.DAYS_OF_WEEK;
    }
}
