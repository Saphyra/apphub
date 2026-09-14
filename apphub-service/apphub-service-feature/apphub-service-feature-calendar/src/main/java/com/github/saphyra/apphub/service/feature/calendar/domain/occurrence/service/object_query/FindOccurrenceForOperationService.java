package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service.object_query;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.feature.calendar.common.Operation;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
class FindOccurrenceForOperationService {
    private final OccurrenceGrantFinder occurrenceGrantFinder;
    private final EventGrantFinder eventGrantFinder;

    BiWrapper<Event, Occurrence> findOccurrence(UUID userId, UUID eventId, UUID occurrenceId, Operation operation) {
        Set<Grant> requiredGrants = operation.getRequiredGrants();
        log.info("Required grants: {}", requiredGrants);

        BiWrapper<Occurrence, Set<Grant>> occurrenceWithGrants = occurrenceGrantFinder.getOccurrenceWithGrants(userId, eventId, occurrenceId);
        log.info("Occurrence grants: {}", occurrenceWithGrants.getEntity2());
        Occurrence occurrence = occurrenceWithGrants.getEntity1();

        BiWrapper<Event, Set<Grant>> eventWithGrants = eventGrantFinder.getEventWithGrants(userId, eventId);
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

        if (aggregatedGrants.containsAll(requiredGrants)) {
            return new BiWrapper<>(event, occurrence);
        } else {
            throw ExceptionFactory.notFound("User " + userId + " does not have required grants " + Arrays.toString(requiredGrants.toArray()) + " for occurrence " + occurrenceId + " to perform operation " + operation);
        }
    }
}
