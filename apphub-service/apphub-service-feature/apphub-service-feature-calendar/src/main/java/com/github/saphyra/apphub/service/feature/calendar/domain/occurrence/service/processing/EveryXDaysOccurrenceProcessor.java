package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service.processing;

import com.github.saphyra.apphub.api.feature.calendar.model.RepetitionType;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.deprecated_dao.DeprecatedOccurrenceDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.deprecated_dao.DeprecatedOccurrenceFactory;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service.condition.RepetitionTypeConditionSelector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
class EveryXDaysOccurrenceProcessor extends AbstractOccurrenceProcessor {
    EveryXDaysOccurrenceProcessor(DeprecatedOccurrenceFactory occurrenceFactory, DeprecatedOccurrenceDao occurrenceDao, RepetitionTypeConditionSelector repetitionTypeConditionSelector, DateTimeUtil dateTimeUtil) {
        super(occurrenceFactory, occurrenceDao, repetitionTypeConditionSelector, dateTimeUtil);
    }

    @Override
    public RepetitionType getRepetitionType() {
        return RepetitionType.EVERY_X_DAYS;
    }
}
