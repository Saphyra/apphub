package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service.object_query;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class FindOccurrenceService {
    private final EventGrantFinder eventGrantFinder;
    private final OccurrenceGrantFinder occurrenceGrantFinder;

    BiWrapper<Event, Occurrence> findOccurrence(UUID userId, UUID eventId, UUID occurrenceId) {
        BiWrapper<Occurrence, Set<Grant>> occurrenceWithGrants = occurrenceGrantFinder.getOccurrenceWithGrants(userId, eventId, occurrenceId);
        log.info("Occurrence grants: {}", occurrenceWithGrants.getEntity2());
        Occurrence occurrence = occurrenceWithGrants.getEntity1();

        BiWrapper<Event, Set<Grant>> eventWithGrants = eventGrantFinder.getEventWithGrants(userId, occurrence.getEventId());
        log.info("Event grants: {}", eventWithGrants.getEntity2());
        Event event = eventWithGrants.getEntity1();

        Set<Grant> aggregatedGrants = Stream.concat(
                occurrenceWithGrants.getEntity2().stream(),
                eventWithGrants.getEntity2()
                    .stream()
                    .flatMap(Grant::projectForChildren)
            )
            .collect(Collectors.toSet());
        log.info("Aggregated grants: {}", aggregatedGrants);

        if (!aggregatedGrants.contains(Grant.VIEW) && !aggregatedGrants.contains(Grant.SEE)) {
            throw ExceptionFactory.forbiddenOperation("User " + userId + " does not have access toor occurrence " + occurrenceId);
        }

        return new BiWrapper<>(
            event.setMasked(!eventWithGrants.getEntity2().contains(Grant.VIEW)),
            occurrence.setMasked(!aggregatedGrants.contains(Grant.VIEW))
        );
    }
}
