package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service.object_query;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.LabelEventMapping;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.service.LabelObjectQueryService;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.OccurrenceDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
class GetOccurrencesOfUserServiceHelper {
    private final AlmDao almDao;
    private final EventLabelMappingDao eventLabelMappingDao;
    private final OccurrenceDao occurrenceDao;
    private final EventDao eventDao;
    private final LabelObjectQueryService labelObjectQueryService;

    List<Occurrence> getOccurrences(UUID userId) {
        return Stream.of(
                getOwnOccurrences(userId),
                getSharedOccurrences(userId),
                getSharedEventOccurrences(userId),
                getSharedLabelOccurrences(userId),
                getVisibleLabelOccurrences(userId)
            )
            .flatMap(s -> s)
            .distinct()
            .toList();
    }

    //Return occurrences of events created by another users using the user's shared label.
    Stream<Occurrence> getVisibleLabelOccurrences(UUID userId) {
        return labelObjectQueryService.getByUserId(userId)
            .filter(bw -> bw.getEntity2().contains(Grant.VIEW_CHILDREN) || bw.getEntity2().contains(Grant.SEE_CHILDREN))
            .map(BiWrapper::getEntity1)
            .flatMap(label -> eventLabelMappingDao.getEventsOfLabel(label.getUserId(), label.getLabelId()).map(LabelEventMapping::getEventIds).orElse(Map.of()).keySet().stream())
            .flatMap(eventId -> occurrenceDao.getByEventId(eventId).stream());
    }

    Stream<Occurrence> getSharedLabelOccurrences(UUID userId) {
        return almDao.getByUserIdAndObjectType(userId, SharedObjectType.LABEL)
            .stream()
            .filter(alm -> alm.getGrants().contains(Grant.VIEW_CHILDREN) || alm.getGrants().contains(Grant.SEE_CHILDREN))
            .flatMap(alm -> eventLabelMappingDao.getEventsOfLabel(alm.getOwner(), alm.getObjectId()).map(LabelEventMapping::getEventIds).orElse(Map.of()).keySet().stream())
            .flatMap(eventId -> occurrenceDao.getByEventId(eventId).stream());
    }

    Stream<Occurrence> getSharedEventOccurrences(UUID userId) {
        return almDao.getByUserIdAndObjectType(userId, SharedObjectType.EVENT)
            .stream()
            .filter(alm -> alm.getGrants().contains(Grant.VIEW_CHILDREN) || alm.getGrants().contains(Grant.SEE_CHILDREN))
            .flatMap(alm -> occurrenceDao.getByEventId(alm.getObjectId()).stream());
    }

    Stream<Occurrence> getSharedOccurrences(UUID userId) {
        return almDao.getByUserIdAndObjectType(userId, SharedObjectType.OCCURRENCE)
            .stream()
            .filter(alm -> alm.getGrants().contains(Grant.VIEW) || alm.getGrants().contains(Grant.SEE))
            .map(alm -> occurrenceDao.findByIdValidated(alm.getParent(), alm.getObjectId()));
    }

    Stream<Occurrence> getOwnOccurrences(UUID userId) {
        return eventDao.getByUserId(userId)
            .stream()
            .flatMap(event -> occurrenceDao.getByEventId(event.getEventId()).stream());
    }
}
