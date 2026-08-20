package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service.object_query;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.TriWrapper;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventFactory;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.LabelEventMapping;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.PrincipalType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
class EventGrantFinder {
    private final EventDao eventDao;
    private final AlmDao almDao;
    private final EventLabelMappingDao eventLabelMappingDao;
    private final EventFactory eventFactory;

    /**
     * @return the event, and all the grants of the event combined from the Alm of the event and Alms of labels of the events.
     */
    BiWrapper<Event, Set<Grant>> getEventWithGrants(UUID userId, UUID eventId) {
        //Look for own event
        Optional<Event> maybeEvent = eventDao.findById(userId, eventId);
        if (maybeEvent.isPresent()) {
            Set<Grant> grants = Grant.forType(SharedObjectType.EVENT);
            log.info("Own event found with grants: {}", grants);
            return new BiWrapper<>(maybeEvent.get(), grants);
        }

        //Look for shared event
        Optional<BiWrapper<Event, Set<Grant>>> maybeSharedEvent = almDao.findForObject(userId, PrincipalType.USER, eventId, SharedObjectType.EVENT)
            //Query event for Alm and pair it with the grants from Alm
            .flatMap(alm -> eventDao.findById(alm.getOwner(), eventId)
                .map(event -> {
                        Set<Grant> grants = alm.getGrants();
                        log.info("Shared event found with grants: {}", grants);
                        return new BiWrapper<>(event, grants);
                    }
                ));

        //Look for shared label
        Optional<BiWrapper<Event, Set<Grant>>> maybeSharedLabelEvent = almDao.getByUserIdAndObjectType(userId, SharedObjectType.LABEL)
            .stream()
            //Filter for labels of event
            .map(alm -> new BiWrapper<>(alm, eventLabelMappingDao.getEventsOfLabel(alm.getOwner(), alm.getObjectId()).map(LabelEventMapping::getEventIds).orElse(Map.of())))
            .filter(bw -> bw.getEntity2().containsKey(eventId))
            //Pair eventId with grants of its Alm
            .map(bw -> new TriWrapper<>(bw.getEntity1().getGrants(), bw.getEntity2().get(eventId), eventId))
            //Merge all grants of the same eventId into one list
            .reduce((a, b) -> {
                    Set<Grant> aggregatedGrants = Stream.concat(
                            a.getEntity1().stream(),
                            b.getEntity1().stream()
                        )
                        .collect(Collectors.toSet());

                    return new TriWrapper<>(aggregatedGrants, a.getEntity2(), a.getEntity3());
                }
            )
            //Project grants for children
            .map(tw -> {
                    Set<Grant> projectedGrants = tw.getEntity1()
                        .stream()
                        .flatMap(Grant::projectForChildren)
                        .collect(Collectors.toSet());

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
                    .collect(Collectors.toSet())
            ))
            .orElseGet(() -> new BiWrapper<>(eventFactory.dummyEvent(userId, eventId), Set.of()));
    }
}
