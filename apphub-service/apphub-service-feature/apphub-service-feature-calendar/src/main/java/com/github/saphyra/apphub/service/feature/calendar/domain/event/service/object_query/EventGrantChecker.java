package com.github.saphyra.apphub.service.feature.calendar.domain.event.service.object_query;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.Alm;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.PrincipalType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
class EventGrantChecker {
    private final AlmDao almDao;
    private final EventLabelMappingDao eventLabelMappingDao;

    boolean hasGrants(UUID userId, Event event, Collection<Grant> grants) {
        //User's own event
        if (userId.equals(event.getUserId())) {
            return true;
        }

        Set<Grant> sharedEventGrants = almDao.findForObject(userId, PrincipalType.USER, event.getEventId(), SharedObjectType.EVENT)
            .map(Alm::getGrants)
            .orElse(Set.of());

        //Shared event has all the necessary grants
        if (sharedEventGrants.containsAll(grants)) {
            return true;
        }

        //Get grants from labels of event that can be inherited by the event
        Set<Grant> sharedLabelGrants = eventLabelMappingDao.getLabelsOfEvent(event.getUserId(), event.getEventId())
            .getLabelIds()
            .keySet()
            .stream()
            .map(labelId -> almDao.findForObject(userId, PrincipalType.USER, labelId, SharedObjectType.LABEL))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .flatMap(alm -> alm.getGrants().stream())
            .flatMap(Grant::projectForChildren)
            .collect(Collectors.toSet());

        Set<Grant> aggregatedGrants = Stream.concat(sharedEventGrants.stream(), sharedLabelGrants.stream())
            .collect(Collectors.toSet());

        return aggregatedGrants.containsAll(grants);
    }
}
