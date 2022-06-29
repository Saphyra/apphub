package com.github.saphyra.apphub.service.diary.service.occurrence;

import com.github.saphyra.apphub.api.diary.model.OccurrenceResponse;
import com.github.saphyra.apphub.service.diary.dao.occurance.Occurrence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class OccurrenceToResponseConverter {
    public List<OccurrenceResponse> convert(List<Occurrence> occurrences) {
        return occurrences.stream()
            .map(this::convert)
            .collect(Collectors.toList());
    }

    public OccurrenceResponse convert(Occurrence occurrence) {
        return OccurrenceResponse.builder()
            .occurrenceId(occurrence.getOccurrenceId())
            .eventId(occurrence.getEventId())
            .status(occurrence.getStatus().name())
            .note(occurrence.getNote())
            .build();
    }
}
