package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.api.feature.calendar.model.OccurrenceStatus;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.api.feature.calendar.model.request.OccurrenceRequest;
import com.github.saphyra.apphub.api.feature.calendar.model.response.OccurrenceResponse;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.OccurrenceDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.PrincipalType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class EditOccurrenceService {
    private final OccurrenceRequestValidator occurrenceRequestValidator;
    private final OccurrenceDao occurrenceDao;
    private final OccurrenceResponseMapper occurrenceResponseMapper;
    private final EventDao eventDao;
    private final AlmDao almDao;
    private final EventLabelMappingDao eventLabelMappingDao;

    public void editOccurrence(UUID userId, UUID eventId, UUID occurrenceId, OccurrenceRequest request) {
        occurrenceRequestValidator.validate(request);

        Occurrence occurrence = occurrenceDao.findByIdValidated(eventId, occurrenceId);
        Event event = eventDao.findById(userId, eventId)
            .or(() -> almDao.findForObject(userId, PrincipalType.USER, eventId, SharedObjectType.EVENT).flatMap(alm -> eventDao.findById(alm.getOwner(), eventId)))
            .or(() -> getEventOfSharedLabel(userId, eventId))
            .orElseThrow(() -> ExceptionFactory.notFound("Event " + eventId + " does not exist or not available for user " + userId));

        occurrence.setDate(request.getDate());
        occurrence.setTime(nullIfEquals(request.getTime(), event.getTime()));
        occurrence.setStatus(request.getStatus());
        occurrence.setNote(request.getNote());
        occurrence.setRemindMeBeforeDays(nullIfEquals(request.getRemindMeBeforeDays(), event.getRemindMeBeforeDays()));
        occurrence.setReminded(request.getReminded());
        occurrence.setAutoDone(nullIfEquals(request.getAutoDone(), event.isAutoDone()));

        occurrenceDao.save(occurrence);
    }

    //TODO verify access
    private Optional<Event> getEventOfSharedLabel(UUID userId, UUID eventId) {
        return almDao.getByUserIdAndObjectType(userId, SharedObjectType.LABEL)
            .stream()
            .map(alm -> eventLabelMappingDao.getEventsOfLabel(alm.getOwner(), alm.getObjectId()))
            .flatMap(mapping -> mapping.getEventIds().entrySet().stream())
            .filter(mapping -> mapping.getKey().equals(eventId))
            .findFirst()
            .flatMap(mapping -> eventDao.findById(mapping.getValue(), mapping.getKey()));
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

    public OccurrenceResponse editOccurrenceStatus(UUID userId, UUID eventId, UUID occurrenceId, OccurrenceStatus status) {
        ValidationUtil.notNull(status, "status");

        Occurrence occurrence = occurrenceDao.findByIdValidated(eventId, occurrenceId);
        occurrence.setStatus(status);
        occurrenceDao.save(occurrence);

        return occurrenceResponseMapper.toResponse(userId, occurrence);
    }

    public OccurrenceResponse setReminded(UUID userId, UUID eventId, UUID occurrenceId) {
        Occurrence occurrence = occurrenceDao.findByIdValidated(eventId, occurrenceId);
        occurrence.setReminded(true);
        occurrenceDao.save(occurrence);

        return occurrenceResponseMapper.toResponse(userId, occurrence);
    }
}
