package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.api.feature.calendar.model.OccurrenceStatus;
import com.github.saphyra.apphub.api.feature.calendar.model.request.OccurrenceRequest;
import com.github.saphyra.apphub.api.feature.calendar.model.response.OccurrenceResponse;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service.object_query.OccurrenceObjectQueryService;
import com.github.saphyra.apphub.service.feature.calendar.common.Operation;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.OccurrenceDao;
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
    private final OccurrenceResponseMapper occurrenceResponseMapper;
    private final OccurrenceObjectQueryService occurrenceObjectQueryService;

    public void editOccurrence(UUID userId, UUID eventId, UUID occurrenceId, OccurrenceRequest request) {
        occurrenceRequestValidator.validate(request);

        BiWrapper<Event, Occurrence> bw = occurrenceObjectQueryService.findOccurrence(userId, eventId, occurrenceId, Operation.EDIT);
        Event event = bw.getEntity1();
        Occurrence occurrence = bw.getEntity2();

        occurrence.setDate(request.getDate());
        occurrence.setTime(nullIfEquals(request.getTime(), event.getTime()));
        occurrence.setStatus(request.getStatus());
        occurrence.setNote(request.getNote());
        occurrence.setRemindMeBeforeDays(nullIfEquals(request.getRemindMeBeforeDays(), event.getRemindMeBeforeDays()));
        occurrence.setReminded(request.getReminded());
        occurrence.setAutoDone(nullIfEquals(request.getAutoDone(), event.isAutoDone()));

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

    public OccurrenceResponse editOccurrenceStatus(UUID userId, UUID eventId, UUID occurrenceId, OccurrenceStatus status) {
        ValidationUtil.notNull(status, "status");

        Occurrence occurrence = occurrenceObjectQueryService.findOccurrence(userId, eventId, occurrenceId, Operation.EDIT)
            .getEntity2();
        occurrence.setStatus(status);
        occurrenceDao.save(occurrence);

        //Re-query is needed because the occurrence and event queried for edition does not contain if records have to be masked or not
        BiWrapper<Event, Occurrence> bw = occurrenceObjectQueryService.findOccurrence(userId, eventId, occurrenceId);

        return occurrenceResponseMapper.toResponse(userId, bw.getEntity1(), bw.getEntity2());
    }

    public OccurrenceResponse setReminded(UUID userId, UUID eventId, UUID occurrenceId) {
        Occurrence occurrence = occurrenceObjectQueryService.findOccurrence(userId, eventId, occurrenceId, Operation.EDIT)
            .getEntity2();
        occurrence.setReminded(true);
        occurrenceDao.save(occurrence);

        //Re-query is needed because the occurrence and event queried for edition does not contain if records have to be masked or not
        BiWrapper<Event, Occurrence> bw = occurrenceObjectQueryService.findOccurrence(userId, eventId, occurrenceId);

        return occurrenceResponseMapper.toResponse(userId, bw.getEntity1(), bw.getEntity2());
    }
}
