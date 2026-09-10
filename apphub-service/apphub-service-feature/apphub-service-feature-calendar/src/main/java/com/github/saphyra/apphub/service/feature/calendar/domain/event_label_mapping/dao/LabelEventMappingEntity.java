package com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
@Builder
class LabelEventMappingEntity {
    private final String userId;
    private final String labelId;
    private final Map<String, String> eventIds; //Map<EventId, UserId>
}
