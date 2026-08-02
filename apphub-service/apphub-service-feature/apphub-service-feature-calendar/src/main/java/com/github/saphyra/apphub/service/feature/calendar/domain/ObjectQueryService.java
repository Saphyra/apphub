package com.github.saphyra.apphub.service.feature.calendar.domain;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.Alm;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.PrincipalType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
//TODO unit test
public class ObjectQueryService {
    private final EventDao eventDao;
    private final AlmDao almDao;
    private final EventLabelMappingDao eventLabelMappingDao;

    public Optional<Event> findEvent(UUID userId, UUID eventId, Operation operation) {
        //Check if event is own. If yes, return it without further checks
        Optional<Event> maybeEvent = eventDao.findById(userId, eventId);
        if (maybeEvent.isPresent()) {
            return maybeEvent;
        }

        //Check if event is shared with user. If yes, check if user has the required grant for the operation. If yes, return the event.
        Optional<Alm> maybeAlm = almDao.findForObject(userId, PrincipalType.USER, eventId, SharedObjectType.EVENT);
        if (maybeAlm.isPresent()) {
            Alm alm = maybeAlm.get();
            if (operation == Operation.DELETE && alm.getGrants().contains(Grant.DELETE)) {
                return eventDao.findById(alm.getOwner(), alm.getObjectId());
            }
        }

        //Get Labels shared with the user
        return almDao.getByUserIdAndObjectType(userId, SharedObjectType.LABEL)
            .stream()
            //Get the events of the shared labels
            .map(alm -> new BiWrapper<>(alm, eventLabelMappingDao.getEventsOfLabel(alm.getOwner(), alm.getObjectId())))
            //Filter for labels of event
            .filter(bw -> bw.getEntity2().getEventIds().containsKey(eventId))
            //Filter for label that allows the user deleting its event
            .filter(bw -> bw.getEntity1().getGrants().contains(Grant.DELETE_CHILDREN))
            .findAny()
            //Query the event
            .flatMap(bw -> {
                UUID eventsOwnerId = bw.getEntity2()
                    .getEventIds()
                    .get(eventId);
                return eventDao.findById(eventsOwnerId, eventId);
            });
    }
}
