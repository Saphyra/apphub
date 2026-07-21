package com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@AllArgsConstructor
@Data
@Builder
class EventLabelMappingEntity {
    private final String userId;
    private final String eventId;
    private final Map<String, String> labelIds; //Map<LabelId, UserId>
}
