package com.github.saphyra.apphub.service.calendar.domain.event.service;

import com.github.saphyra.apphub.api.calendar.model.response.EventResponse;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.dao.EventLabelMapping;
import com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class EventMapper {
    private final ObjectMapperWrapper objectMapperWrapper;
    private final EventLabelMappingDao eventLabelMappingDao;

     EventResponse toResponse(Event event) {
        return EventResponse.builder()
            .eventId(event.getEventId())
            .repetitionType(event.getRepetitionType())
            .repetitionData(objectMapperWrapper.readValue(event.getRepetitionData(), Object.class))
            .repeatForDays(event.getRepeatForDays())
            .startDate(event.getStartDate())
            .endDate(event.getEndDate())
            .time(event.getTime())
            .title(event.getTitle())
            .content(event.getContent())
            .remindMeBeforeDays(event.getRemindMeBeforeDays())
            .labels(getLabels(event.getEventId()))
            .build();
    }

    private List<UUID> getLabels(UUID eventId) {
        return eventLabelMappingDao.getByEventId(eventId)
            .stream()
            .map(EventLabelMapping::getLabelId)
            .toList();
    }
}
