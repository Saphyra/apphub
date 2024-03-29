package com.github.saphyra.apphub.service.calendar.service.occurrence.service;

import com.github.saphyra.apphub.api.calendar.model.OccurrenceResponse;
import com.github.saphyra.apphub.service.calendar.dao.event.Event;
import com.github.saphyra.apphub.service.calendar.dao.event.EventDao;
import com.github.saphyra.apphub.service.calendar.dao.occurance.Occurrence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class OccurrenceToResponseConverter {
    private final EventDao eventDao;

    public List<OccurrenceResponse> convert(List<Occurrence> occurrences) {
        return occurrences.stream()
            .map(this::convert)
            .collect(Collectors.toList());
    }

    public OccurrenceResponse convert(Occurrence occurrence) {
        Event event = eventDao.findByIdValidated(occurrence.getEventId());

        return OccurrenceResponse.builder()
            .date(occurrence.getDate())
            .time(occurrence.getTime())
            .occurrenceId(occurrence.getOccurrenceId())
            .eventId(occurrence.getEventId())
            .status(occurrence.getStatus().name())
            .title(event.getTitle())
            .content(event.getContent())
            .note(occurrence.getNote())
            .build();
    }
}
