package com.github.saphyra.apphub.service.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.api.calendar.model.RepetitionType;
import com.github.saphyra.apphub.service.calendar.common.context.UpdateEventContext;

public interface OccurrenceRecreator {
    RepetitionType getRepetitionType();

    void recreateOccurrences(UpdateEventContext context);
}
