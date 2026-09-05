package com.github.saphyra.apphub.service.feature.calendar.domain.event.service.object_query;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
class GetLabellessEventsService {
    private final EventLabelMappingDao eventLabelMappingDao;
    private final EventDao eventDao;
    private final AlmDao almDao;

    Stream<Event> getLabellessEvents(UUID userId) {
        Stream<Event> ownEvents = eventLabelMappingDao.getLabelsOfEventsByUserId(userId)
            .stream()
            .filter(eventLabelMapping -> eventLabelMapping.getLabelIds().isEmpty())
            .map(eventLabelMapping -> eventDao.findByIdValidated(userId, eventLabelMapping.getEventId()));

        Stream<Event> sharedEvents = almDao.getByUserIdAndObjectType(userId, SharedObjectType.EVENT)
            .stream()
            .filter(alm -> alm.getGrants().contains(Grant.VIEW) || alm.getGrants().contains(Grant.SEE))
            .filter(alm -> eventLabelMappingDao.getLabelsOfEvent(alm.getOwner(), alm.getObjectId()).getLabelIds().isEmpty())
            .map(alm -> eventDao.findByIdValidated(alm.getOwner(), alm.getObjectId()).setMasked(!alm.getGrants().contains(Grant.VIEW)));

        return Stream.concat(ownEvents, sharedEvents);
    }
}
