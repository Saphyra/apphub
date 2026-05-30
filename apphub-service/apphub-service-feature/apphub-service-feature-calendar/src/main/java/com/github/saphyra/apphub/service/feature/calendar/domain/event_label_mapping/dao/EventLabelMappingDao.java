package com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
//TODO unit test
public class EventLabelMappingDao {
    public List<UUID> getEventsOfLabel(UUID userId, UUID labelId) {
        return null;
    }

    public Map<UUID, List<UUID>> getByEventIds(UUID userId, Collection<UUID> eventIds) {
        return null;
    }

    public List<UUID> getByEventId(UUID userId, UUID eventId) {
        return null;
    }

    /**
     *
     * @return Map<EventId, List<LabelId>>
     */
    public Map<UUID, List<UUID>> getLabelsOfEventByUserId(UUID userId) {
        return null;
    }

    public void saveLabelsOfEvent(UUID userId, UUID eventId, List<UUID> labels) {

    }
}
