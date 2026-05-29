package com.github.saphyra.apphub.service.feature.calendar.domain.event.service;

import com.github.saphyra.apphub.api.feature.calendar.model.response.EventResponse;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao.DeprecatedEvent;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.deprecated_dao.DeprecatedEventLabelMapping;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.deprecated_dao.DeprecatedEventLabelMappingDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class EventMapper {
    private final ObjectMapper objectMapper;
    private final DeprecatedEventLabelMappingDao eventLabelMappingDao;

     EventResponse toResponse(DeprecatedEvent event) {
        return EventResponse.builder()
            .eventId(event.getEventId())
            .repetitionType(event.getRepetitionType())
            .repetitionData(Optional.ofNullable(event.getRepetitionData()).map(repetitionData -> objectMapper.readValue(repetitionData, Object.class)).orElse(null))
            .repeatForDays(event.getRepeatForDays())
            .startDate(event.getStartDate())
            .endDate(event.getEndDate())
            .time(event.getTime())
            .title(event.getTitle())
            .content(event.getContent())
            .remindMeBeforeDays(event.getRemindMeBeforeDays())
            .labels(getLabels(event.getEventId()))
            .archived(event.isArchived())
            .build();
    }

    private List<UUID> getLabels(UUID eventId) {
        return eventLabelMappingDao.getByEventId(eventId)
            .stream()
            .map(DeprecatedEventLabelMapping::getLabelId)
            .toList();
    }
}
