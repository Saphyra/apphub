package com.github.saphyra.apphub.service.calendar.service.occurrence.service.edit;

import com.github.saphyra.apphub.api.calendar.model.CalendarResponse;
import com.github.saphyra.apphub.api.calendar.model.EditOccurrenceRequest;
import com.github.saphyra.apphub.service.calendar.dao.event.Event;
import com.github.saphyra.apphub.service.calendar.dao.event.EventDao;
import com.github.saphyra.apphub.service.calendar.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.calendar.dao.occurance.OccurrenceDao;
import com.github.saphyra.apphub.service.calendar.dao.occurance.OccurrenceStatus;
import com.github.saphyra.apphub.service.calendar.service.calendar.CalendarQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
@Slf4j
public class EditOccurrenceService {
    private final OccurrenceDao occurrenceDao;
    private final EventDao eventDao;
    private final CalendarQueryService calendarQueryService;
    private final EditOccurrenceRequestValidator editOccurrenceRequestValidator;

    @Transactional
    public List<CalendarResponse> edit(UUID userId, UUID occurrenceId, EditOccurrenceRequest request) {
        editOccurrenceRequestValidator.validate(request);

        Occurrence occurrence = occurrenceDao.findByIdValidated(occurrenceId);
        Event event = eventDao.findByIdValidated(occurrence.getEventId());

        event.setTitle(request.getTitle());
        event.setContent(request.getContent());
        occurrence.setNote(request.getNote());
        if (occurrence.getStatus() == OccurrenceStatus.VIRTUAL && !isBlank(request.getNote())) {
            occurrence.setStatus(OccurrenceStatus.PENDING);
        }

        eventDao.save(event);
        occurrenceDao.save(occurrence);

        return calendarQueryService.getCalendar(userId, request.getReferenceDate());
    }
}
