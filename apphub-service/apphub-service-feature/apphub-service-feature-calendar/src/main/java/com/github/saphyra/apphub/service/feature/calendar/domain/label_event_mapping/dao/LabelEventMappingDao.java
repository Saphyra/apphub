package com.github.saphyra.apphub.service.feature.calendar.domain.label_event_mapping.dao;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class LabelEventMappingDao {
    private final LabelEventMappingConverter labelEventMappingConverter;
    private final LabelEventMappingRepository repository;
    private final UuidConverter uuidConverter;
    private final LabelEventMappingCache labelEventMappingCache;

    public Map<UUID, LabelEventMapping> getByUserId(UUID userId) {
        return labelEventMappingCache.get(
            userId,
            () -> labelEventMappingConverter.convertEntity(repository.getByUserId(uuidConverter.convertDomain(userId)))
                .stream()
                .collect(Collectors.toMap(LabelEventMapping::getLabelId, o -> o))
        );
    }

    public Optional<LabelEventMapping> getEventsOfLabel(UUID userId, UUID labelId) {
        return Optional.ofNullable(getByUserId(userId).get(labelId));
    }

    public void saveEventsOfLabel(LabelEventMapping mapping) {
        repository.save(labelEventMappingConverter.convertDomain(mapping));
        labelEventMappingCache.invalidate(mapping.getUserId());
    }

    public void saveEventsOfLabels(List<LabelEventMapping> mappings) {
        repository.save(labelEventMappingConverter.convertDomain(mappings));
        mappings.forEach(mapping -> labelEventMappingCache.invalidate(mapping.getUserId()));
    }

    public void deleteByEventId(UUID userId, List<UUID> eventIds) {
        List<LabelEventMapping> modifiedMappings = getByUserId(userId)
            .values()
            .stream()
            .filter(labelEventMapping -> eventIds.stream().anyMatch(eventId -> labelEventMapping.getEventIds().containsKey(eventId)))
            .peek(labelEventMapping -> eventIds.forEach(labelEventMapping::removeEvent))
            .toList();
        if (!modifiedMappings.isEmpty()) {
            modifiedMappings.forEach(this::saveEventsOfLabel);
        }
    }

    public void deleteByLabelId(UUID userId, UUID labelId) {
        repository.delete(uuidConverter.convertDomain(userId), uuidConverter.convertDomain(labelId));
        labelEventMappingCache.invalidate(userId);
    }

    public void invalidate(UUID userId) {
        labelEventMappingCache.invalidate(userId);
    }
}
