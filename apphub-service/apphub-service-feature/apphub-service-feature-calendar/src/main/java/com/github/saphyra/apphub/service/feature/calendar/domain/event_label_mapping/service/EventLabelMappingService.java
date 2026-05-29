package com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.service;

import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.deprecated_dao.DeprecatedEventLabelMapping;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.deprecated_dao.DeprecatedEventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.deprecated_dao.DeprecatedEventLabelMappingFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventLabelMappingService {
    private final DeprecatedEventLabelMappingDao eventLabelMappingDao;
    private final DeprecatedEventLabelMappingFactory eventLabelMappingFactory;
    private final LabelIdValidator labelIdValidator;

    public void addLabels(UUID userId, UUID eventId, List<UUID> labels) {
        labelIdValidator.validate(labels);

        List<DeprecatedEventLabelMapping> mappings = labels.stream()
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
            .map(DeprecatedEventLabelMapping::getLabelId)
            .collect(Collectors.toList());
    }

    public void setLabels(UUID userId, UUID eventId, List<UUID> labels) {
        labelIdValidator.validate(labels);

        Map<UUID, DeprecatedEventLabelMapping> existingMappings = eventLabelMappingDao.getByEventId(eventId)
            .stream()
            .collect(Collectors.toMap(DeprecatedEventLabelMapping::getLabelId, mapping -> mapping));

        Stream.concat(existingMappings.keySet().stream(), labels.stream())
            .distinct()
            .forEach(labelId -> {
                if (!existingMappings.containsKey(labelId) && labels.contains(labelId)) {
                    // New mapping needed
                    DeprecatedEventLabelMapping newMapping = eventLabelMappingFactory.create(userId, eventId, labelId);
                    eventLabelMappingDao.save(newMapping);
                } else if (existingMappings.containsKey(labelId) && !labels.contains(labelId)) {
                    // Mapping is not needed anymore
                    eventLabelMappingDao.delete(existingMappings.get(labelId));
                }
            });
    }
}
