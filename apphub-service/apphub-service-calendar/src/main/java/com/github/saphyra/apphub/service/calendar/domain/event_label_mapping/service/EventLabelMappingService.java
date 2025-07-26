package com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.service;

import com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.dao.EventLabelMapping;
import com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.dao.EventLabelMappingFactory;
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
    private final EventLabelMappingFactory eventLabelMappingFactory;
    private final LabelIdValidator labelIdValidator;

    public void addLabels(UUID userId, UUID eventId, List<UUID> labels) {
        labelIdValidator.validate(labels);

        List<EventLabelMapping> mappings = labels.stream()
            .map(labelId -> eventLabelMappingFactory.create(userId, eventId, labelId))
            .toList();

        eventLabelMappingDao.saveAll(mappings);
    }

    public boolean hasLabel(UUID eventId, UUID label) {
        return getLabelIds(eventId)
            .contains(label);
    }

    public List<UUID> getLabelIds(UUID eventId) {
        return eventLabelMappingDao.getByEventId(eventId)
            .stream()
            .map(EventLabelMapping::getLabelId)
            .collect(Collectors.toList());
    }

    public void setLabels(UUID userId, UUID eventId, List<UUID> labels) {
        labelIdValidator.validate(labels);

        List<EventLabelMapping> existingMappings = eventLabelMappingDao.getByEventId(eventId);
        List<EventLabelMapping> newLabels = labels.stream()
            .map(labelId -> eventLabelMappingFactory.create(userId, eventId, labelId))
            .toList();

        List<EventLabelMapping> mappingsToDelete = existingMappings.stream()
            .filter(mapping -> !newLabels.contains(mapping))
            .toList();
        List<EventLabelMapping> mappingsToAdd = newLabels.stream()
            .filter(mapping -> !existingMappings.contains(mapping))
            .toList();

        eventLabelMappingDao.deleteAll(mappingsToDelete);
        eventLabelMappingDao.saveAll(mappingsToAdd);
    }
}
