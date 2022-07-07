package com.github.saphyra.apphub.service.diary.service.occurrence.service;

import com.github.saphyra.apphub.api.diary.model.CalendarResponse;
import com.github.saphyra.apphub.api.diary.model.EditOccurrenceRequest;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.diary.dao.event.Event;
import com.github.saphyra.apphub.service.diary.dao.event.EventDao;
import com.github.saphyra.apphub.service.diary.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.diary.dao.occurance.OccurrenceDao;
import com.github.saphyra.apphub.service.diary.dao.occurance.OccurrenceStatus;
import com.github.saphyra.apphub.service.diary.service.calendar.CalendarQueryService;
import com.github.saphyra.apphub.service.diary.service.event.service.EventTitleValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class EditOccurrenceService {
    private final OccurrenceDao occurrenceDao;
    private final EventTitleValidator eventTitleValidator;
    private final EventDao eventDao;
    private final CalendarQueryService calendarQueryService;

    @Transactional
    public List<CalendarResponse> edit(UUID userId, UUID occurrenceId, EditOccurrenceRequest request) {
        eventTitleValidator.validate(request.getTitle());
        ValidationUtil.notNull(request.getDate(), "date");

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

        return calendarQueryService.getCalendar(userId, request.getDate());
    }
}
