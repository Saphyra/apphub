package com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.deprecated_dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
@Deprecated(forRemoval = true)
public class DeprecatedEventLabelMapping {
    private final UUID eventId;
    private final UUID labelId;
    private final UUID userId;
}
