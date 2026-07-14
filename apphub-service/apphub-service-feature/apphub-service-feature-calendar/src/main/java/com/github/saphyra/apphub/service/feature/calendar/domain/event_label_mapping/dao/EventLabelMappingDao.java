package com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventLabelMappingDao {
    private final UuidConverter uuidConverter;
    private final EventLabelMappingRepository repository;

    public List<UUID> getEventsOfLabel(UUID userId, UUID labelId) {
        return repository.getEventsOfLabel(uuidConverter.convertDomain(userId), uuidConverter.convertDomain(labelId))
            .stream()
            .map(uuidConverter::convertEntity)
            .toList();
    }

    public Map<UUID, List<UUID>> getLabelsOfEvents(UUID userId, Collection<UUID> eventIds) {
        return repository.getLabelsOfEvents(uuidConverter.convertDomain(userId), uuidConverter.convertDomain(eventIds))
            .stream()
            .collect(Collectors.toMap(bw -> uuidConverter.convertEntity(bw.getEntity1()), bw -> uuidConverter.convertEntity(bw.getEntity2())));
    }

    public List<UUID> getLabelsOfEvent(UUID userId, UUID eventId) {
        return getLabelsOfEvents(userId, List.of(eventId))
            .getOrDefault(eventId, List.of());
    }

    /**
     * @return Map<EventId, List<LabelId>>
     */
    public Map<UUID, List<UUID>> getLabelsOfEventsByUserId(UUID userId) {
        return repository.getLabelsOfEventsByUserId(uuidConverter.convertDomain(userId))
            .stream()
            .collect(Collectors.toMap(bw -> uuidConverter.convertEntity(bw.getEntity1()), bw -> uuidConverter.convertEntity(bw.getEntity2())));
    }

    public void saveLabelsOfEvent(UUID userId, UUID eventId, List<UUID> labelIds) {
        repository.saveLabelsOfEvent(
            uuidConverter.convertDomain(userId),
            uuidConverter.convertDomain(eventId),
            uuidConverter.convertDomain(labelIds)
        );
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

        repository.deleteLabelsOfEvents(userIdString, uuidConverter.convertDomain(eventIds));

        List<BiWrapper<String, List<String>>> modifiedMappings = repository.getEventsOfLabelsByUserId(userIdString)
            .stream()
            .filter(bw -> bw.getEntity2().removeIf(eventIdsString::contains))
            .toList();
        if (!modifiedMappings.isEmpty()) {
            repository.saveEventsOfLabels(userIdString, modifiedMappings);
        }
    }

    public void deleteByLabelId(UUID userId, UUID labelId) {
        log.info("Deleting mappings of label {} of user {}.", labelId, userId);

        String userIdString = uuidConverter.convertDomain(userId);
        String labelIdString = uuidConverter.convertDomain(labelId);

        repository.deleteEventsOfLabel(userIdString, labelIdString);

        List<BiWrapper<String, List<String>>> modifiedMappings = repository.getLabelsOfEventsByUserId(userIdString)
            .stream()
            .filter(bw -> bw.getEntity2().removeIf(l -> l.equals(labelIdString)))
            .peek(bw -> log.info("Labels mapped to event {} modified. New labels: {}", bw.getEntity1(), bw.getEntity2()))
            .toList();
        if (!modifiedMappings.isEmpty()) {
            repository.saveLabelsOfEvents(userIdString, modifiedMappings);
        }
    }

    public void saveEventsOfLabel(UUID userId, UUID labelId, List<UUID> eventIds) {
        repository.saveEventsOfLabels(
            uuidConverter.convertDomain(userId),
            uuidConverter.convertDomain(labelId),
            uuidConverter.convertDomain(eventIds)
        );
    }

    public List<BiWrapper<UUID, List<UUID>>> getEventsOfLabels(UUID userId, List<UUID> labelIds) {
        return repository.getEventsOfLabels(uuidConverter.convertDomain(userId), uuidConverter.convertDomain(labelIds))
            .stream()
            .map(bw -> new BiWrapper<>(uuidConverter.convertEntity(bw.getEntity1()), uuidConverter.convertEntity(bw.getEntity2())))
            .toList();
    }

    /**
     *
     * @param mappings List<BiWrapper<LabelId, List<EventId>>>
     */
    public void saveEventsOfLabels(UUID userId, List<BiWrapper<UUID, List<UUID>>> mappings) {
        repository.saveEventsOfLabels(
            uuidConverter.convertDomain(userId),
            mappings.stream()
                .map(bw -> new BiWrapper<>(uuidConverter.convertDomain(bw.getEntity1()), uuidConverter.convertDomain(bw.getEntity2())))
                .toList()
        );
    }
}