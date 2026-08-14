package com.github.saphyra.apphub.service.feature.calendar.domain;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.TriWrapper;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventFactory;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.LabelEventMapping;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.OccurrenceDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.Alm;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.PrincipalType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class OccurrenceObjectQueryService {
    private final OccurrenceDao occurrenceDao;
    private final EventDao eventDao;
    private final AlmDao almDao;
    private final EventLabelMappingDao eventLabelMappingDao;
    private final EventFactory eventFactory;

    public BiWrapper<Event, Occurrence> getOccurrence(UUID userId, UUID eventId, UUID occurrenceId, Operation operation) {
        List<Grant> requiredGrants = operation.getRequiredGrants();
        log.info("Required grants: {}", requiredGrants);

        BiWrapper<Occurrence, List<Grant>> occurrenceWithGrants = getOccurrenceWithGrants(userId, eventId, occurrenceId);
        log.info("Occurrence grants: {}", occurrenceWithGrants.getEntity2());
        Occurrence occurrence = occurrenceWithGrants.getEntity1();

        BiWrapper<Event, List<Grant>> eventWithGrants = getEventWithGrants(userId, occurrence.getEventId());
        log.info("Event grants: {}", eventWithGrants.getEntity2());
        Event event = eventWithGrants.getEntity1();

        List<Grant> aggregatedGrants = Stream.concat(
                occurrenceWithGrants.getEntity2().stream(),
                eventWithGrants.getEntity2().stream()
            )
            .distinct()
            .toList();
        log.info("Aggregated grants: {}", aggregatedGrants);

        if (aggregatedGrants.containsAll(requiredGrants)) {
            return new BiWrapper<>(event, occurrence);
        } else {
            throw ExceptionFactory.forbiddenOperation("User " + userId + " does not have required grants " + Arrays.toString(requiredGrants.toArray()) + " for occurrence " + occurrenceId + " to perform operation " + operation);
        }
    }

    private BiWrapper<Event, List<Grant>> getEventWithGrants(UUID userId, UUID eventId) {
        //Look for own event
        Optional<Event> maybeEvent = eventDao.findById(userId, eventId);
        if (maybeEvent.isPresent()) {
            List<Grant> grants = Grant.forType(SharedObjectType.EVENT);
            log.info("Own event found with grants: {}", grants);
            return new BiWrapper<>(maybeEvent.get(), grants);
        }

        //Look for shared event
        Optional<BiWrapper<Event, List<Grant>>> maybeSharedEvent = almDao.findForObject(userId, PrincipalType.USER, eventId, SharedObjectType.EVENT)
            //Query event for Alm and pair it with the grants from Alm
            .flatMap(alm -> eventDao.findById(alm.getOwner(), eventId)
                .map(event -> {
                        List<Grant> grants = alm.getGrants();
                        log.info("Shared event found with grants: {}", grants);
                        return new BiWrapper<>(event, grants);
                    }
                ));

        //Look for shared label
        Optional<BiWrapper<Event, List<Grant>>> maybeSharedLabelEvent = almDao.getByUserIdAndObjectType(userId, SharedObjectType.LABEL)
            .stream()
            //Filter for labels of event
            .map(alm -> new BiWrapper<>(alm, eventLabelMappingDao.getEventsOfLabel(alm.getOwner(), alm.getObjectId()).map(LabelEventMapping::getEventIds).orElse(Map.of())))
            .filter(bw -> bw.getEntity2().containsKey(eventId))
            //Pair eventId with grants of its Alm
            .map(bw -> new TriWrapper<>(bw.getEntity1().getGrants(), bw.getEntity2().get(eventId), eventId))
            //Merge all grants of the same eventId into one list
            .reduce((a, b) -> new TriWrapper<>(
                Stream.concat(
                        a.getEntity1().stream(),
                        b.getEntity1().stream()
                    )
                    .distinct()
                    .toList(),
                a.getEntity2(),
                a.getEntity3()
            ))
            //Query an event and pair it with the merged grants
            .map(tw -> new BiWrapper<>(eventDao.findByIdValidated(tw.getEntity2(), tw.getEntity3()), tw.getEntity1()));

        //Return the event and the aggregated list of grants, or a dummy event if the event is not shared.
        return Stream.of(maybeSharedEvent, maybeSharedLabelEvent)
            .filter(Optional::isPresent)
            .map(Optional::get)
            //Merge grants from shared event and shared label
            .reduce((a, b) -> new BiWrapper<>(
                a.getEntity1(),
                Stream.concat(
                        a.getEntity2().stream(),
                        b.getEntity2().stream()
                    )
                    .distinct()
                    .toList()
            ))
            //Project grants for children
            .map(bw -> {
                List<Grant> projectedGrants = bw.getEntity2()
                    .stream()
                    .flatMap(Grant::projectForChildren)
                    .distinct()
                    .toList();

                return new BiWrapper<>(bw.getEntity1(), projectedGrants);
            })
            .orElseGet(() -> new BiWrapper<>(eventFactory.dummyEvent(userId, eventId), List.of()));
    }

    /**
     * @return the occurrence with the given id and all grants the user has for it. (All applicable grants if occurrence is user's own)
     */
    private BiWrapper<Occurrence, List<Grant>> getOccurrenceWithGrants(UUID userId, UUID eventId, UUID occurrenceId) {
        Occurrence occurrence = occurrenceDao.findByIdValidated(eventId, occurrenceId);
        List<Grant> grants;
        if (occurrence.getUserId().equals(userId)) {
            grants = Grant.forType(SharedObjectType.OCCURRENCE);
        } else {
            grants = almDao.findForObject(userId, PrincipalType.USER, occurrenceId, SharedObjectType.OCCURRENCE)
                .map(Alm::getGrants)
                .orElse(List.of());
        }

        return new BiWrapper<>(occurrence, grants);
    }
}
