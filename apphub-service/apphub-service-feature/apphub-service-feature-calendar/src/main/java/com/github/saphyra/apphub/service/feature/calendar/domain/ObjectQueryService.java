package com.github.saphyra.apphub.service.feature.calendar.domain;

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
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

@SuppressWarnings("SlowListContainsAll")
@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ObjectQueryService {
    private final EventDao eventDao;
    private final AlmDao almDao;
    private final EventLabelMappingDao eventLabelMappingDao;

    /**
     * Finds event for the user. Returned result must only be used for operations, and must not be returned to the client.
     */
    public Optional<Event> findEvent(UUID userId, UUID eventId, Operation operation) {
        //Check if event is own. If yes, return it without further checks
        Optional<Event> maybeEvent = eventDao.findById(userId, eventId);
        if (maybeEvent.isPresent()) {
            return maybeEvent;
        }

        //Check if event is shared with user. If yes, check if user has the required grant for the operation. If yes, return the event.
        Optional<Alm> maybeAlm = almDao.findForObject(userId, PrincipalType.USER, eventId, SharedObjectType.EVENT);
        if (maybeAlm.isPresent()) {
            Alm alm = maybeAlm.get();
            return eventDao.findById(alm.getOwner(), alm.getObjectId())
                .filter(event -> hasGrants(userId, event, operation.getRequiredGrants()));
        }

        //Get Labels shared with the user
        return almDao.getByUserIdAndObjectType(userId, SharedObjectType.LABEL)
            .stream()
            //Get the events of the shared labels
            .map(alm -> eventLabelMappingDao.getEventsOfLabel(alm.getOwner(), alm.getObjectId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(LabelEventMapping::getEventIds)
            //Filter for labels of event
            .filter(eventIds -> eventIds.containsKey(eventId))
            .map(eventIds -> eventIds.get(eventId))
            .findAny()
            .flatMap(eventsUserId -> eventDao.findById(eventsUserId, eventId))
            .filter(event -> hasGrants(userId, event, operation.getRequiredGrants()));
    }

    private boolean hasGrants(UUID userId, Event event, List<Grant> grants) {
        //User's own event
        if (userId.equals(event.getUserId())) {
            return true;
        }

        List<Grant> sharedEventGrants = almDao.findForObject(userId, PrincipalType.USER, event.getEventId(), SharedObjectType.EVENT)
            .map(Alm::getGrants)
            .orElse(List.of());

        //Shared event has all the necessary grants
        if (sharedEventGrants.containsAll(grants)) {
            return true;
        }

        //List of grants that can be inherited from labels
        List<Grant> parentSearchFor = grants.stream()
            .map(Grant::toParent)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();

        //Get grants from labels of event that can be inherited by the event
        List<Grant> sharedLabelGrants = eventLabelMappingDao.getLabelsOfEvent(event.getUserId(), event.getEventId())
            .getLabelIds()
            .keySet()
            .stream()
            .map(labelId -> almDao.findForObject(userId, PrincipalType.USER, labelId, SharedObjectType.LABEL))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .flatMap(alm -> alm.getGrants().stream())
            .distinct()
            .filter(parentSearchFor::contains)
            .toList();

        for (Grant requiredGrant : grants) {
            Optional<Grant> parentGrant = requiredGrant.toParent();

            if (sharedEventGrants.contains(requiredGrant)) {
                //Shared event has the necessary grant
            } else if (parentGrant.isPresent() && sharedLabelGrants.contains(parentGrant.get())) {
                //Shared label has the necessary parent grant
            } else {
                return false;
            }
        }

        return true;
    }

    public List<Event> getEventsOfLabel(UUID userId, UUID labelId) {
        Optional<LabelEventMapping> maybeLabelEventMapping = eventLabelMappingDao.getEventsOfLabel(userId, labelId);
        Optional<Alm> maybeAlm = Optional.empty();
        if (maybeLabelEventMapping.isPresent()) {
            log.info("Found: {}", maybeLabelEventMapping.get());
        } else {
            log.info("LabelEventMapping not found for userId {} and labelId {}. Check if label is shared with the user.", userId, labelId);
            maybeAlm = almDao.findForObject(userId, PrincipalType.USER, labelId, SharedObjectType.LABEL)
                .map(alm -> {
                    log.info("Found: {}", alm);

                    return alm;
                })
                .filter(alm -> alm.getGrants().contains(Grant.VIEW_CHILDREN) || alm.getGrants().contains(Grant.SEE_CHILDREN))
                .map(alm -> {
                    log.info("Found matching: {}", alm);

                    return alm;
                });
            maybeLabelEventMapping = maybeAlm.flatMap(alm -> eventLabelMappingDao.getEventsOfLabel(alm.getOwner(), labelId));
        }

        if (maybeLabelEventMapping.isPresent()) {
            LabelEventMapping labelEventMapping = maybeLabelEventMapping.get();
            log.info("Found: {}", labelEventMapping);
            Alm alm = maybeAlm.orElse(null);

            return eventDao.getByIds(labelEventMapping.getEventIds().entrySet().stream().map(e -> new BiWrapper<>(e.getValue(), e.getKey())).toList())
                .stream()
                .map(event -> event.setMasked(isMasked_labelAlm(userId, event, alm)))
                .toList();
        } else {
            log.info("LabelEventMapping not found for userId {} and labelId {}. Alm: {}", userId, labelId, maybeAlm);
            return List.of();
        }
    }

    private boolean isMasked_labelAlm(UUID userId, Event event, @Nullable Alm labelAlm) {
        //No masking when own event
        if (userId.equals(event.getUserId())) {
            return false;
        }

        //Event is shared
        if (nonNull(labelAlm)) {
            return !labelAlm.getGrants().contains(Grant.VIEW_CHILDREN)
                //If Label does not allow viewing the event, check if the event is explicitly shared with view grant
                && almDao.findForObject(userId, PrincipalType.USER, event.getEventId(), SharedObjectType.EVENT)
                .filter(eventAlm -> eventAlm.getGrants().contains(Grant.VIEW))
                .isEmpty();
        }

        throw new IllegalStateException("Event is not owned by the user and Alm is null. UserId: " + userId + " eventId: " + event.getEventId());
    }


    public List<Event> getEvents(UUID userId) {
        List<BiWrapper<Event, Boolean>> ownEvents = eventDao.getByUserId(userId)
            .stream()
            .map(event -> new BiWrapper<>(event, false))
            .toList();

        List<BiWrapper<Event, Boolean>> sharedEvents = almDao.getByUserIdAndObjectType(userId, SharedObjectType.EVENT)
            .stream()
            .filter(alm -> alm.getGrants().contains(Grant.VIEW) || alm.getGrants().contains(Grant.SEE))
            .map(alm -> new BiWrapper<>(eventDao.findByIdValidated(alm.getOwner(), alm.getObjectId()), !alm.getGrants().contains(Grant.VIEW)))
            .toList();

        List<BiWrapper<Event, Boolean>> eventsOfSharedLabels = almDao.getByUserIdAndObjectType(userId, SharedObjectType.LABEL)
            .stream()
            .filter(alm -> alm.getGrants().contains(Grant.SEE_CHILDREN) || alm.getGrants().contains(Grant.VIEW_CHILDREN))
            .flatMap(alm -> eventLabelMappingDao.getEventsOfLabel(alm.getOwner(), alm.getObjectId())
                .map(LabelEventMapping::getEventIds)
                .orElse(Map.of())
                .entrySet()
                .stream()
                .map(entry -> {
                    UUID eventId = entry.getKey();
                    UUID eventsOwnerId = entry.getValue();

                    Event event = eventDao.findByIdValidated(eventsOwnerId, eventId);
                    boolean isMasked = !userId.equals(event.getUserId()) && !alm.getGrants().contains(Grant.VIEW_CHILDREN);

                    return new BiWrapper<>(event, isMasked);
                }))
            .toList();

        return Stream.of(ownEvents, sharedEvents, eventsOfSharedLabels)
            .flatMap(Collection::stream)
            .collect(Collectors.toMap(BiWrapper::getEntity1, BiWrapper::getEntity2, (isMasked1, isMasked2) -> isMasked1 && isMasked2))
            .entrySet()
            .stream()
            .map(entry -> entry.getKey().setMasked(entry.getValue()))
            .toList();
    }

    /**
     * Finds event for the user. Result contains information about the data to be masked before returning it to the client
     */
    public Optional<Event> findEvent(UUID userId, UUID eventId) {
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
                .map(event -> event.setMasked(isMasked_eventAlm(userId, event, alm)));
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

    private boolean isMasked_eventAlm(UUID userId, Event event, Alm eventAlm) {
        Supplier<Boolean> eventAlmMasking = () -> !eventAlm.getGrants().contains(Grant.VIEW);
        Supplier<Boolean> labelAlmMasking = () -> {
            //Get labels shared with user
            return almDao.getByUserIdAndObjectType(userId, SharedObjectType.LABEL)
                .stream()
                //Filter labels that allow viewing events
                .filter(alm -> alm.getGrants().contains(Grant.VIEW_CHILDREN))
                //Get events for the labels
                .map(alm -> eventLabelMappingDao.getEventsOfLabel(alm.getOwner(), alm.getObjectId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .flatMap(labelEventMapping -> labelEventMapping.getEventIds().keySet().stream())
                .noneMatch(eventId -> eventId.equals(event.getEventId()));
        };

        return eventAlmMasking.get() && labelAlmMasking.get();
    }
}
