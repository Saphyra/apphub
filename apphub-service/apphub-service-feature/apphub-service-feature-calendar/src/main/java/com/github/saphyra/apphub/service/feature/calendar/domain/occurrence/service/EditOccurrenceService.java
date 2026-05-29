package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.api.feature.calendar.model.OccurrenceStatus;
import com.github.saphyra.apphub.api.feature.calendar.model.request.OccurrenceRequest;
import com.github.saphyra.apphub.api.feature.calendar.model.response.OccurrenceResponse;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao.DeprecatedEvent;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao.DeprecatedEventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.deprecated_dao.DeprecatedOccurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.deprecated_dao.DeprecatedOccurrenceDao;
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
    private final DeprecatedOccurrenceDao occurrenceDao;
    private final OccurrenceMapper occurrenceMapper;
    private final DeprecatedEventDao eventDao;

    public void editOccurrence(UUID occurrenceId, OccurrenceRequest request) {
        occurrenceRequestValidator.validate(request);

        DeprecatedOccurrence occurrence = occurrenceDao.findByIdValidated(occurrenceId);
        DeprecatedEvent event = eventDao.findByIdValidated(occurrence.getEventId());

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

        DeprecatedOccurrence occurrence = occurrenceDao.findByIdValidated(occurrenceId);
        occurrence.setStatus(status);
        occurrenceDao.save(occurrence);

        return occurrenceMapper.toResponse(occurrence);
    }

    public OccurrenceResponse setReminded(UUID occurrenceId) {
        DeprecatedOccurrence occurrence = occurrenceDao.findByIdValidated(occurrenceId);
        occurrence.setReminded(true);
        occurrenceDao.save(occurrence);

        return occurrenceMapper.toResponse(occurrence);
    }
}
