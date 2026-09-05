package com.github.saphyra.apphub.service.feature.calendar.domain.event.service.object_query;

import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.service.feature.calendar.common.Operation;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.LabelEventMapping;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.Alm;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.PrincipalType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class FindEventForOperationService {
    private final EventDao eventDao;
    private final AlmDao almDao;
    private final EventLabelMappingDao eventLabelMappingDao;
    private final EventGrantChecker eventGrantChecker;

    /**
     * Finds event for the user. Returned result must only be used for operations, and must not be returned to the client.
     */
    Optional<Event> findEvent(UUID userId, UUID eventId, Operation operation) {
        //Check if event is own. If yes, return it without further checks
        Optional<Event> maybeEvent = eventDao.findById(userId, eventId);
        if (maybeEvent.isPresent()) {
            return maybeEvent;
        }

        //Check if event is shared with user. If yes, check if user has the required grant for the operation. If yes, return the event.
        Optional<Alm> maybeAlm = almDao.findForObject(userId, PrincipalType.USER, eventId, SharedObjectType.EVENT);
        if (maybeAlm.isPresent()) {
            Alm alm = maybeAlm.get();
            return eventDao.findById(alm.getOwner(), alm.getObjectId())
                .filter(event -> eventGrantChecker.hasGrants(userId, event, operation.getRequiredGrants()));
        }

        //Get Labels shared with the user
        return almDao.getByUserIdAndObjectType(userId, SharedObjectType.LABEL)
            .stream()
            //Get the events of the shared labels
            .map(alm -> eventLabelMappingDao.getEventsOfLabel(alm.getOwner(), alm.getObjectId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(LabelEventMapping::getEventIds)
            //Filter for labels of event
            .filter(eventIds -> eventIds.containsKey(eventId))
            .map(eventIds -> eventIds.get(eventId))
            .findAny()
            .flatMap(eventsUserId -> eventDao.findById(eventsUserId, eventId))
            .filter(event -> eventGrantChecker.hasGrants(userId, event, operation.getRequiredGrants()));
    }
}
