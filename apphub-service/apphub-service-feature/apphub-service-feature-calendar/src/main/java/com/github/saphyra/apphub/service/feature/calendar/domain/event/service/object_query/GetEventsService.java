package com.github.saphyra.apphub.service.feature.calendar.domain.event.service.object_query;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.LabelEventMapping;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
class GetEventsService {
    private final EventDao eventDao;
    private final AlmDao almDao;
    private final EventLabelMappingDao eventLabelMappingDao;

    List<Event> getEvents(UUID userId) {
        //Query own events
        List<BiWrapper<Event, Boolean>> ownEvents = eventDao.getByUserId(userId)
            .stream()
            .map(event -> new BiWrapper<>(event, false))
            .toList();

        //Query shared events
        List<BiWrapper<Event, Boolean>> sharedEvents = almDao.getByUserIdAndObjectType(userId, SharedObjectType.EVENT)
            .stream()
            .filter(alm -> alm.getGrants().contains(Grant.VIEW) || alm.getGrants().contains(Grant.SEE))
            .map(alm -> new BiWrapper<>(eventDao.findByIdValidated(alm.getOwner(), alm.getObjectId()), !alm.getGrants().contains(Grant.VIEW)))
            .toList();

        //Query events of shared labels
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
}
