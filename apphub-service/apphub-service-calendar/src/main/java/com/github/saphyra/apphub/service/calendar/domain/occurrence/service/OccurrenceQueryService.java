package com.github.saphyra.apphub.service.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.api.calendar.model.response.OccurrenceResponse;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.OccurrenceDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class OccurrenceQueryService {
    private final OccurrenceDao occurrenceDao;
    private final OccurrenceMapper occurrenceMapper;

    public List<OccurrenceResponse> getOccurrences(UUID eventId) {
        return occurrenceDao.getByEventId(eventId)
            .stream()
            .map(occurrenceMapper::toResponse)
            .toList();
    }
}
