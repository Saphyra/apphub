package com.github.saphyra.apphub.service.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.service.calendar.common.context.UpdateEventContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class RecreateOccurrenceService {
    private final List<OccurrenceRecreator> occurrenceRecreators;

    /**
     * After major modification of the event (e.g. changing the repetition type), the occurrences need to be recreated.
     */
    public void recreateOccurrences(UpdateEventContext context) {
        occurrenceRecreators.stream()
            .filter(recreator -> recreator.getRepetitionType() == context.getEvent().getRepetitionType())
            .findAny()
            .orElseThrow(() -> new IllegalStateException("No OccurrenceRecreator found for repetition type: " + context.getEvent().getRepetitionType()))
            .recreateOccurrences(context);
    }
}
