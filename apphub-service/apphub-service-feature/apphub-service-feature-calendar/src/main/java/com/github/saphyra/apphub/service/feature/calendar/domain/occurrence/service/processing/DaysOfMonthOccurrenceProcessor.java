package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service.processing;

import com.github.saphyra.apphub.api.feature.calendar.model.RepetitionType;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.deprecated_dao.DeprecatedOccurrenceDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.deprecated_dao.DeprecatedOccurrenceFactory;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service.condition.RepetitionTypeConditionSelector;
import org.springframework.stereotype.Component;

@Component
class DaysOfMonthOccurrenceProcessor extends AbstractOccurrenceProcessor {
    DaysOfMonthOccurrenceProcessor(DeprecatedOccurrenceFactory occurrenceFactory, DeprecatedOccurrenceDao occurrenceDao, RepetitionTypeConditionSelector repetitionTypeConditionSelector, DateTimeUtil dateTimeUtil) {
        super(occurrenceFactory, occurrenceDao, repetitionTypeConditionSelector, dateTimeUtil);
    }

    @Override
    public RepetitionType getRepetitionType() {
        return RepetitionType.DAYS_OF_MONTH;
    }
}
