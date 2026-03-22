package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.service.feature.calendar.common.context.UpdateEventContext;

public interface OccurrenceRecreator extends OccurrenceRepetitionTypeAware {
    /**
     * Modifies list of occurrences of the given event - Creates new ones if the dates changed, and deletes existing ones, if the modified event does not cover them anymore.
     */
    void recreateOccurrences(UpdateEventContext context);
}
