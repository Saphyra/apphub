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
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
//TODO facadify
public class OccurrenceObjectQueryService {
    private final OccurrenceDao occurrenceDao;
    private final EventDao eventDao;
    private final AlmDao almDao;
    private final EventLabelMappingDao eventLabelMappingDao;
    private final EventFactory eventFactory;

    public BiWrapper<Event, Occurrence> findOccurrence(UUID userId, UUID eventId, UUID occurrenceId) {
        BiWrapper<Occurrence, List<Grant>> occurrenceWithGrants = getOccurrenceWithGrants(userId, eventId, occurrenceId);
        log.info("Occurrence grants: {}", occurrenceWithGrants.getEntity2());
        Occurrence occurrence = occurrenceWithGrants.getEntity1();

        BiWrapper<Event, List<Grant>> eventWithGrants = getEventWithGrants(userId, occurrence.getEventId());
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

    public BiWrapper<Event, Occurrence> findOccurrence(UUID userId, UUID eventId, UUID occurrenceId, Operation operation) {
        List<Grant> requiredGrants = operation.getRequiredGrants();
        log.info("Required grants: {}", requiredGrants);

        BiWrapper<Occurrence, List<Grant>> occurrenceWithGrants = getOccurrenceWithGrants(userId, eventId, occurrenceId);
        log.info("Occurrence grants: {}", occurrenceWithGrants.getEntity2());
        Occurrence occurrence = occurrenceWithGrants.getEntity1();

        BiWrapper<Event, List<Grant>> eventWithGrants = getEventWithGrants(userId, occurrence.getEventId());
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
            throw ExceptionFactory.forbiddenOperation("User " + userId + " does not have required grants " + Arrays.toString(requiredGrants.toArray()) + " for occurrence " + occurrenceId + " to perform operation " + operation);
        }
    }

    /**
     * @return the event, and all the grants of the event combined from the Alm of the event and Alms of labels of the events.
     */
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
            //Project grants for children
            .map(tw -> {
                    List<Grant> projectedGrants = tw.getEntity1()
                        .stream()
                        .flatMap(Grant::projectForChildren)
                        .distinct()
                        .toList();

                    return new TriWrapper<>(projectedGrants, tw.getEntity2(), tw.getEntity3());
                }
            )
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

    public Map<Event, List<Occurrence>> getOccurrences(UUID userId) {
        List<Occurrence> occurrences = Stream.of(
                getOwnOccurrences(userId),
                getSharedOccurrences(userId),
                getSharedEventOccurrences(userId),
                getSharedLabelOccurrences(userId)
            )
            .flatMap(s -> s)
            .distinct()
            .toList();

        return occurrences.stream()
            .map(Occurrence::getEventId)
            .distinct()
            .map(eventId -> getEventWithGrants(userId, eventId))
            .map(bw -> {
                Event event = bw.getEntity1();
                List<Grant> eventGrants = bw.getEntity2();
                log.info("Grants of event {}: {}", event.getEventId(), eventGrants);

                if (!eventGrants.contains(Grant.VIEW)) {
                    log.info("User {} does not have VIEW grant for event {}, masking event", userId, event.getEventId());
                    event.setMasked(true);
                }

                List<Occurrence> eventOccurrences = occurrences.stream()
                    .filter(occurrence -> occurrence.getEventId().equals(event.getEventId()))
                    .peek(occurrence -> {
                        if (
                            //Own event or shared event allows viewing children
                            !eventGrants.contains(Grant.VIEW_CHILDREN)
                                //Own occurrence
                                && !occurrence.getUserId().equals(userId)
                                //Shared occurrence allows viewing
                                && almDao.findForObject(userId, PrincipalType.USER, occurrence.getOccurrenceId(), SharedObjectType.OCCURRENCE).filter(alm -> alm.getGrants().contains(Grant.VIEW)).isEmpty()
                        ) {
                            log.info("User {} does not have VIEW grant for occurrence {}, masking occurrence", userId, occurrence.getOccurrenceId());
                            occurrence.setMasked(true);
                        }
                    })
                    .toList();

                return new BiWrapper<>(event, eventOccurrences);
            })
            .collect(Collectors.toMap(BiWrapper::getEntity1, BiWrapper::getEntity2));
    }

    private Stream<Occurrence> getSharedLabelOccurrences(UUID userId) {
        return almDao.getByUserIdAndObjectType(userId, SharedObjectType.LABEL)
            .stream()
            .filter(alm -> alm.getGrants().contains(Grant.VIEW_CHILDREN) || alm.getGrants().contains(Grant.SEE_CHILDREN))
            .flatMap(alm -> eventLabelMappingDao.getEventsOfLabel(alm.getOwner(), alm.getObjectId()).map(LabelEventMapping::getEventIds).orElse(Map.of()).keySet().stream())
            .flatMap(eventId -> occurrenceDao.getByEventId(eventId).stream());
    }

    private Stream<Occurrence> getSharedEventOccurrences(UUID userId) {
        return almDao.getByUserIdAndObjectType(userId, SharedObjectType.EVENT)
            .stream()
            .filter(alm -> alm.getGrants().contains(Grant.VIEW_CHILDREN) || alm.getGrants().contains(Grant.SEE_CHILDREN))
            .flatMap(alm -> occurrenceDao.getByEventId(alm.getObjectId()).stream());
    }

    private Stream<Occurrence> getSharedOccurrences(UUID userId) {
        return almDao.getByUserIdAndObjectType(userId, SharedObjectType.OCCURRENCE)
            .stream()
            .filter(alm -> alm.getGrants().contains(Grant.VIEW) || alm.getGrants().contains(Grant.SEE))
            .map(alm -> occurrenceDao.findByIdValidated(alm.getParent(), alm.getObjectId()));
    }

    private Stream<Occurrence> getOwnOccurrences(UUID userId) {
        return eventDao.getByUserId(userId)
            .stream()
            .flatMap(event -> occurrenceDao.getByEventId(event.getEventId()).stream());
    }

    public BiWrapper<Event, List<Occurrence>> getOccurrences(UUID userId, UUID eventId) {
        List<Occurrence> occurrences = occurrenceDao.getByEventId(eventId);
        Map<UUID, List<Grant>> occurrenceIdGrantsMapping = almDao.getByUserIdAndObjectType(userId, SharedObjectType.OCCURRENCE)
            .stream()
            .collect(Collectors.toMap(Alm::getObjectId, Alm::getGrants));
        BiWrapper<Event, List<Grant>> eventWithGrants = getEventWithGrants(userId, eventId);
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
                        || occurrenceIdGrantsMapping.getOrDefault(occurrence.getOccurrenceId(), List.of()).contains(Grant.SEE) //Occurrence is explicitly shared
                        || occurrenceIdGrantsMapping.getOrDefault(occurrence.getOccurrenceId(), List.of()).contains(Grant.VIEW) //Occurrence is explicitly shared
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
                    if (occurrenceIdGrantsMapping.getOrDefault(occurrence.getOccurrenceId(), List.of()).contains(Grant.VIEW)) {
                        return occurrence;
                    }

                    //Occurrence is not viewable
                    return occurrence.setMasked(true);
                })
                .toList()
        );
    }
}
