package com.github.saphyra.apphub.service.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.calendar.domain.event.service.UpdateEventContext;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.Occurrence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class RecreateOccurrenceService {
    public void recreateOccurrences(UpdateEventContext context, Event event, List<Occurrence> occurrences) {
        // TODO implement
    }
}
