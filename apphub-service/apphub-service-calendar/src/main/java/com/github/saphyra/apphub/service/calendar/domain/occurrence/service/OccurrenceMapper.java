package com.github.saphyra.apphub.service.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.api.calendar.model.response.OccurrenceResponse;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.Occurrence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class OccurrenceMapper {
    public OccurrenceResponse toResponse(Occurrence occurrence) {
        return OccurrenceResponse.builder()
            .occurrenceId(occurrence.getOccurrenceId())
            .eventId(occurrence.getEventId())
            .date(occurrence.getDate())
            .time(occurrence.getTime())
            .status(occurrence.getStatus())
            .note(occurrence.getNote())
            .remindMeBeforeDays(occurrence.getRemindMeBeforeDays())
            .reminded(occurrence.getReminded())
            .build();
    }
}
