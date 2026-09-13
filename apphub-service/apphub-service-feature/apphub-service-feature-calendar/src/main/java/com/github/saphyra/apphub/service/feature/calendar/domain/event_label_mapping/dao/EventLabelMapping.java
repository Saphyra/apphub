package com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@Builder
public class EventLabelMapping {
    private final UUID userId;
    private final UUID eventId;
    private final Map<UUID, UUID> labelIds; //Map<LabelId, UserId>

    public EventLabelMapping removeLabelId(UUID labelId) {
        return new EventLabelMapping(
            userId,
            eventId,
            labelIds.entrySet()
                .stream()
                .filter(entry -> !entry.getKey().equals(labelId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        );
    }
}
