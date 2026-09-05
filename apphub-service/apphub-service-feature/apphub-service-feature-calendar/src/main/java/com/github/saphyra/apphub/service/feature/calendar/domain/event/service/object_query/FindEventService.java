package com.github.saphyra.apphub.service.feature.calendar.domain.event.service.object_query;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.LabelEventMapping;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.Alm;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.PrincipalType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class FindEventService {
    private final EventDao eventDao;
    private final AlmDao almDao;
    private final EventLabelMappingDao eventLabelMappingDao;
    private final EventMaskedChecker eventMaskedChecker;

    /**
     * Finds event for the user. Result contains information about the data to be masked before returning it to the client
     */
    Optional<Event> findEvent(UUID userId, UUID eventId) {
        //Check if event is own. If yes, return it without further checks
        Optional<Event> maybeEvent = eventDao.findById(userId, eventId);
        if (maybeEvent.isPresent()) {
            return maybeEvent;
        }

        //Check if event is shared with the user and user has necessary grants.
        Optional<Alm> maybeAlm = almDao.findForObject(userId, PrincipalType.USER, eventId, SharedObjectType.EVENT)
            .filter(alm -> alm.getGrants().contains(Grant.VIEW) || alm.getGrants().contains(Grant.SEE));
        if (maybeAlm.isPresent()) {
            Alm alm = maybeAlm.get();

            return eventDao.findById(alm.getOwner(), alm.getObjectId())
                .map(event -> event.setMasked(eventMaskedChecker.isMasked_eventAlm(userId, event, alm)));
        }

        //Get Labels shared with the user
        return almDao.getByUserIdAndObjectType(userId, SharedObjectType.LABEL)
            .stream()
            //Filter for label that allows the user viewing its event
            .filter(alm -> alm.getGrants().contains(Grant.VIEW_CHILDREN) || alm.getGrants().contains(Grant.SEE_CHILDREN))
            //Get the events of the shared labels
            .map(alm -> new BiWrapper<>(alm, eventLabelMappingDao.getEventsOfLabel(alm.getOwner(), alm.getObjectId()).map(LabelEventMapping::getEventIds).orElse(Map.of())))
            //Filter for labels of event
            .filter(bw -> bw.getEntity2().containsKey(eventId))
            //BiWrapper<EventsOwnerId, Masked>
            .map(bw -> new BiWrapper<>(bw.getEntity2().get(eventId), !bw.getEntity1().getGrants().contains(Grant.VIEW_CHILDREN)))
            //Compare grants to see if data has to be masked
            .collect(Collectors.toMap(BiWrapper::getEntity1, BiWrapper::getEntity2, (isMasked1, isMasked2) -> isMasked1 && isMasked2))
            .entrySet()
            .stream()
            .map(entry -> eventDao.findByIdValidated(entry.getKey(), eventId).setMasked(entry.getValue()))
            .findFirst();
    }
}
