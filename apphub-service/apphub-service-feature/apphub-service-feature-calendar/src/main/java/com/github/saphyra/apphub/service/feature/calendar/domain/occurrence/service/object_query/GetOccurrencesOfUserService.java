package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service.object_query;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.LabelEventMapping;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.OccurrenceDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.PrincipalType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class GetOccurrencesOfUserService {
    private final EventGrantFinder eventGrantFinder;
    private final AlmDao almDao;
    private final EventLabelMappingDao eventLabelMappingDao;
    private final OccurrenceDao occurrenceDao;
    private final EventDao eventDao;

    Map<Event, List<Occurrence>> getOccurrences(UUID userId) {
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
            .map(eventId -> eventGrantFinder.getEventWithGrants(userId, eventId))
            .map(bw -> {
                Event event = bw.getEntity1();
                Set<Grant> eventGrants = bw.getEntity2();
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
}
