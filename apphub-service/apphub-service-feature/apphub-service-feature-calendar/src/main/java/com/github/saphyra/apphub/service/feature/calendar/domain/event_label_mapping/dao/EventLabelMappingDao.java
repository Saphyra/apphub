package com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
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
public class EventLabelMappingDao {
    private final UuidConverter uuidConverter;
    private final EventLabelMappingRepository repository;
    private final EventLabelMappingConverter eventLabelMappingConverter;
    private final EventLabelMappingCache eventLabelMappingCache;

    /**
     * @param ids List<BiWrapper<UserId, EventId>></UserId,>
     */
    public List<EventLabelMapping> getLabelsOfEvents(List<BiWrapper<UUID, UUID>> ids) {
        return ids.stream()
            .map(bw -> getLabelsOfEvent(bw.getEntity1(), bw.getEntity2()))
            .toList();
    }

    public EventLabelMapping getLabelsOfEvent(UUID userId, UUID eventId) {
        return Optional.ofNullable(getByUserId(userId).get(eventId))
            .orElseGet(() -> EventLabelMapping.builder()
                .userId(userId)
                .eventId(eventId)
                .labelIds(Map.of())
                .build());
    }

    public Map<UUID, EventLabelMapping> getByUserId(UUID userId) {
        return eventLabelMappingCache.get(
            userId,
            () -> eventLabelMappingConverter.convertEntity(repository.getByUserId(uuidConverter.convertDomain(userId)))
                .stream()
                .collect(Collectors.toMap(EventLabelMapping::getEventId, e -> e))
        );
    }

    public void saveLabelsOfEvent(EventLabelMapping mapping) {
        repository.saveLabelsOfEvent(eventLabelMappingConverter.convertDomain(mapping));
        eventLabelMappingCache.invalidate(mapping.getUserId());
    }

    /**
     * <ul>
     *     <li>Delete labels of events mapping</li>
     *     <li>Delete eventIds from events of labels mapping</li>
     * </ul>
     */
    public void deleteByEventId(UUID userId, List<UUID> eventIds) {
        String userIdString = uuidConverter.convertDomain(userId);
        List<String> eventIdsString = uuidConverter.convertDomain(eventIds);

        repository.deleteLabelsOfEvents(userIdString, eventIdsString);
        eventLabelMappingCache.invalidate(userId);
    }

    public void deleteByLabelId(UUID userId, UUID labelId) {
        log.info("Deleting EventLabelMapping of user {} and label {}.", userId, labelId);

        List<EventLabelMapping> modifiedMappings = getByUserId(userId)
            .values()
            .stream()
            .filter(bw -> bw.getLabelIds().containsKey(labelId))
            .map(mapping -> mapping.removeLabelId(labelId))
            .peek(bw -> log.info("Labels mapped to event {} modified. New labels: {}", bw.getEventId(), bw.getLabelIds()))
            .toList();
        if (!modifiedMappings.isEmpty()) {
            modifiedMappings.forEach(this::saveLabelsOfEvent);
        }
    }

    public void invalidate(UUID userId) {
        eventLabelMappingCache.invalidate(userId);
    }
}