package com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class EventLabelMapping {
    private final UUID eventId;
    private final UUID labelId;
    private final UUID userId;
}
