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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class GetOccurrencesOfEventService {
    private final OccurrenceDao occurrenceDao;
    private final EventGrantFinder eventGrantFinder;
    private final AlmDao almDao;

    BiWrapper<Event, List<Occurrence>> getOccurrences(UUID userId, UUID eventId) {
        Map<UUID, Set<Grant>> occurrenceIdGrantsMapping = getOccurrenceIdsWithGrants(userId);

        BiWrapper<Event, Set<Grant>> eventWithGrants = eventGrantFinder.getEventWithGrants(userId, eventId);
        log.info("Event {} found with grants: {}", eventWithGrants.getEntity1().getEventId(), eventWithGrants.getEntity2());

        Set<Grant> grantsFromEvent = eventWithGrants.getEntity2()
            .stream()
            .flatMap(Grant::projectForChildren)
            .collect(Collectors.toSet());
        log.info("Grants from event {} for occurrences: {}", eventWithGrants.getEntity1().getEventId(), grantsFromEvent);

        Event event = eventWithGrants.getEntity1()
            .setMasked(!eventWithGrants.getEntity2().contains(Grant.VIEW));

        List<Occurrence> occurrences = occurrenceDao.getByEventId(eventId)
            .stream()
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
            .toList();

        return new BiWrapper<>(event, occurrences);
    }

    private Map<UUID, Set<Grant>> getOccurrenceIdsWithGrants(UUID userId) {
        Map<UUID, Set<Grant>> occurrenceIdGrantsMapping = almDao.getByUserIdAndObjectType(userId, SharedObjectType.OCCURRENCE)
            .stream()
            .collect(Collectors.toMap(Alm::getObjectId, Alm::getGrants));
        if (occurrenceIdGrantsMapping.isEmpty()) {
            log.info("No shared occurrences found for user: {}", userId);
        } else {
            occurrenceIdGrantsMapping.forEach((occurrenceId, grants) -> log.info("Shared occurrence found: {} for userId: {} with grants: {}", occurrenceId, userId, grants));
        }
        return occurrenceIdGrantsMapping;
    }
}
