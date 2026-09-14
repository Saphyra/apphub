package com.github.saphyra.apphub.service.feature.calendar.domain.label_event_mapping.dao;

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
        if (eventIds.containsKey(eventId)) {
            if (!(eventIds instanceof HashMap<UUID, UUID>)) {
                this.eventIds = new HashMap<>(eventIds);
            }

            eventIds.remove(eventId);
        }
    }
}
