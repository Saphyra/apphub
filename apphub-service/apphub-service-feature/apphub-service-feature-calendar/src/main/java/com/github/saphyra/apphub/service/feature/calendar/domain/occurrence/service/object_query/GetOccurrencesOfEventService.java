package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service.object_query;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.OccurrenceDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.Alm;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
//TODO unit test
class GetOccurrencesOfEventService {
    private final OccurrenceDao occurrenceDao;
    private final EventGrantFinder eventGrantFinder;
    private final AlmDao almDao;

    BiWrapper<Event, List<Occurrence>> getOccurrences(UUID userId, UUID eventId) {
        List<Occurrence> occurrences = occurrenceDao.getByEventId(eventId);
        Map<UUID, Set<Grant>> occurrenceIdGrantsMapping = almDao.getByUserIdAndObjectType(userId, SharedObjectType.OCCURRENCE)
            .stream()
            .collect(Collectors.toMap(Alm::getObjectId, Alm::getGrants));
        BiWrapper<Event, Set<Grant>> eventWithGrants = eventGrantFinder.getEventWithGrants(userId, eventId);
        Set<Grant> grantsFromEvent = eventWithGrants.getEntity2()
            .stream()
            .flatMap(Grant::projectForChildren)
            .collect(Collectors.toSet());

        return new BiWrapper<>(
            eventWithGrants.getEntity1()
                .setMasked(!eventWithGrants.getEntity2().contains(Grant.VIEW)),
            occurrences.stream()
                .filter(occurrence ->
                    occurrence.getUserId().equals(userId) //Own occurrence
                        || grantsFromEvent.contains(Grant.SEE_CHILDREN) //Event grants access
                        || grantsFromEvent.contains(Grant.VIEW_CHILDREN) //Event grants access
                        || occurrenceIdGrantsMapping.getOrDefault(occurrence.getOccurrenceId(), Set.of()).contains(Grant.SEE) //Occurrence is explicitly shared
                        || occurrenceIdGrantsMapping.getOrDefault(occurrence.getOccurrenceId(), Set.of()).contains(Grant.VIEW) //Occurrence is explicitly shared
                )
                .map(occurrence -> {
                    //User can view own occurrence
                    if (occurrence.getUserId().equals(userId)) {
                        return occurrence;
                    }

                    //Shared event allows viewing occurrences
                    if (grantsFromEvent.contains(Grant.VIEW_CHILDREN)) {
                        return occurrence;
                    }

                    //Explicitly shared occurrence allows viewing
                    if (occurrenceIdGrantsMapping.getOrDefault(occurrence.getOccurrenceId(), Set.of()).contains(Grant.VIEW)) {
                        return occurrence;
                    }

                    //Occurrence is not viewable
                    return occurrence.setMasked(true);
                })
                .toList()
        );
    }
}
