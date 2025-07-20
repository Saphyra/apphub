package com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.service;

import com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.dao.EventLabelMapping;
import com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class EventLabelMappingService {
    private final EventLabelMappingDao eventLabelMappingDao;

    public void addLabels(UUID userId, UUID eventId, List<UUID> labels) {
        //TODO implement
    }

    public boolean hasLabel(UUID userId, UUID eventId, UUID label) {
        //TODO implement
        return false;
    }

    public List<UUID> getLabelIds(UUID eventId) {
        return eventLabelMappingDao.getByEventId(eventId)
            .stream()
            .map(EventLabelMapping::getLabelId)
            .collect(Collectors.toList());
    }

    public void setLabels(UUID userId, UUID eventId, List<UUID> labels) {
        //TODO implement
    }
}
