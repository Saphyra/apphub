package com.github.saphyra.apphub.service.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.api.calendar.model.OccurrenceStatus;
import com.github.saphyra.apphub.api.calendar.model.request.OccurrenceRequest;
import com.github.saphyra.apphub.api.calendar.model.response.OccurrenceResponse;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.OccurrenceDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class EditOccurrenceService {
    private final OccurrenceRequestValidator occurrenceRequestValidator;
    private final OccurrenceDao occurrenceDao;
    private final OccurrenceMapper occurrenceMapper;
    private final EventDao eventDao;

    public void editOccurrence(UUID occurrenceId, OccurrenceRequest request) {
        occurrenceRequestValidator.validate(request);

        Occurrence occurrence = occurrenceDao.findByIdValidated(occurrenceId);
        Event event = eventDao.findByIdValidated(occurrence.getEventId());

        occurrence.setDate(request.getDate());
        occurrence.setTime(nullIfEquals(request.getTime(), event.getTime()));
        occurrence.setStatus(request.getStatus());
        occurrence.setNote(request.getNote());
        occurrence.setRemindMeBeforeDays(nullIfEquals(request.getRemindMeBeforeDays(), event.getRemindMeBeforeDays()));
        occurrence.setReminded(request.getReminded());

        occurrenceDao.save(occurrence);
    }

    /*
       UI does not send null, and does not know if value is from parent or not.
       So if client sends the same value as parent, leave it null, so it is still inherited from parent.
     */
    private <T> T nullIfEquals(T fromRequest, T fromEvent) {
        if (Objects.equals(fromRequest, fromEvent)) {
            return null;
        }

        return fromRequest;
    }

    public OccurrenceResponse editOccurrenceStatus(UUID occurrenceId, OccurrenceStatus status) {
        ValidationUtil.notNull(status, "status");

        Occurrence occurrence = occurrenceDao.findByIdValidated(occurrenceId);
        occurrence.setStatus(status);
        occurrenceDao.save(occurrence);

        return occurrenceMapper.toResponse(occurrence);
    }

    public OccurrenceResponse setReminded(UUID occurrenceId) {
        Occurrence occurrence = occurrenceDao.findByIdValidated(occurrenceId);
        occurrence.setReminded(true);
        occurrenceDao.save(occurrence);

        return occurrenceMapper.toResponse(occurrence);
    }
}
