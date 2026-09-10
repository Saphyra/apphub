package com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class EventLabelMapping {
    private final UUID userId;
    private final UUID eventId;
    private final Map<UUID, UUID> labelIds; //Map<LabelId, UserId>
}
