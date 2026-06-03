package com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
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
        String userIdString = uuidConverter.convertDomain(userId);

        return Lists.partition(uuidConverter.convertDomain(eventIds), Constants.DYNAMO_DB_QUERY_MAX_BATCH_SIZE)
            .stream()
            .flatMap(eventIdsStrings -> repository.getLabelsOfEvents(userIdString, eventIdsStrings).stream())
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

        Lists.partition(eventIds, Constants.DYNAMO_DB_WRITE_MAX_BATCH_SIZE)
            .forEach(batch -> repository.deleteLabelsOfEvents(userIdString, uuidConverter.convertDomain(batch)));

        List<BiWrapper<String, List<String>>> modifiedMappings = repository.getEventsOfLabelsByUserId(userIdString)
            .stream()
            .filter(bw -> bw.getEntity2().removeIf(eventIdsString::contains))
            .toList();
        if (!modifiedMappings.isEmpty()) {
            repository.saveEventsOfLabels(userIdString, modifiedMappings);
        }
    }

    public void deleteByLabelId(UUID userId, UUID labelId) {
        String userIdString = uuidConverter.convertDomain(userId);
        String labelIdString = uuidConverter.convertDomain(labelId);

        repository.deleteEventsOfLabel(userIdString, labelIdString);

        List<BiWrapper<String, List<String>>> modifiedMappings = repository.getLabelsOfEventsByUserId(userIdString)
            .stream()
            .filter(bw -> bw.getEntity2().removeIf(l -> l.equals(labelIdString)))
            .toList();
        if (!modifiedMappings.isEmpty()) {
            repository.saveEventsOfLabels(userIdString, modifiedMappings);
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