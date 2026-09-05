package com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class LabelEventMapping {
    private final UUID userId;
    private final UUID labelId;
    private Map<UUID, UUID> eventIds; //Map<EventId, UserId>

    public void addEvent(UUID userId, UUID eventId) {
        Map<UUID, UUID> map = new HashMap<>(eventIds);
        map.put(eventId, userId);

        this.eventIds = map;
    }

    public void removeEvent(UUID eventId) {
        Map<UUID, UUID> map = new HashMap<>(eventIds);
        map.remove(eventId);

        this.eventIds = map;
    }
}
