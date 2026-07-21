package com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class EventLabelMappingDao {
    private final UuidConverter uuidConverter;
    private final EventLabelMappingRepository repository;
    private final LabelEventMappingConverter labelEventMappingConverter;
    private final EventLabelMappingConverter eventLabelMappingConverter;

    public LabelEventMapping getEventsOfLabel(UUID userId, UUID labelId) {
        return labelEventMappingConverter.convertEntity(repository.getEventsOfLabel(uuidConverter.convertDomain(userId), uuidConverter.convertDomain(labelId)));
    }

    public List<EventLabelMapping> getLabelsOfEvents(UUID userId, Collection<UUID> eventIds) {
        return eventLabelMappingConverter.convertEntity(repository.getLabelsOfEvents(uuidConverter.convertDomain(userId), uuidConverter.convertDomain(eventIds)));
    }

    public EventLabelMapping getLabelsOfEvent(UUID userId, UUID eventId) {
        return eventLabelMappingConverter.convertEntity(repository.getLabelsOfEvent(uuidConverter.convertDomain(userId), uuidConverter.convertDomain(eventId)));
    }

    public List<EventLabelMapping> getLabelsOfEventsByUserId(UUID userId) {
        return eventLabelMappingConverter.convertEntity(repository.getLabelsOfEventsByUserId(uuidConverter.convertDomain(userId)));
    }

    public void saveLabelsOfEvent(EventLabelMapping mapping) {
        repository.saveLabelsOfEvent(eventLabelMappingConverter.convertDomain(mapping));
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

        List<LabelEventMappingEntity> modifiedMappings = repository.getEventsOfLabelsByUserId(userIdString)
            .stream()
            .filter(labelEventMapping -> {
                List<Boolean> removed = eventIdsString.stream()
                    .map(eventId -> nonNull(labelEventMapping.getEventIds().remove(eventId)))
                    .toList();

                return removed.stream().anyMatch(Boolean::booleanValue);
            })
            .toList();
        if (!modifiedMappings.isEmpty()) {
            repository.saveEventsOfLabels(modifiedMappings);
        }
    }

    public void deleteByLabelId(UUID userId, UUID labelId) {
        log.info("Deleting mappings of label {} of user {}.", labelId, userId);

        String userIdString = uuidConverter.convertDomain(userId);
        String labelIdString = uuidConverter.convertDomain(labelId);

        repository.deleteEventsOfLabel(userIdString, labelIdString);

        List<EventLabelMappingEntity> modifiedMappings = repository.getLabelsOfEventsByUserId(userIdString)
            .stream()
            //If item was removed, remove returns the item. If returned item not null, item was removed, so record was modified
            .filter(bw -> nonNull(bw.getLabelIds().remove(labelIdString)))
            .peek(bw -> log.info("Labels mapped to event {} modified. New labels: {}", bw.getEventId(), bw.getLabelIds()))
            .toList();
        if (!modifiedMappings.isEmpty()) {
            repository.saveLabelsOfEvents(modifiedMappings);
        }
    }

    public void saveEventsOfLabel(LabelEventMapping mapping) {
        repository.saveEventsOfLabels(labelEventMappingConverter.convertDomain(mapping));
    }

    public List<LabelEventMapping> getEventsOfLabels(UUID userId, List<UUID> labelIds) {
        return labelEventMappingConverter.convertEntity(repository.getEventsOfLabels(uuidConverter.convertDomain(userId), uuidConverter.convertDomain(labelIds)));
    }

    public void saveEventsOfLabels(List<LabelEventMapping> mappings) {
        repository.saveEventsOfLabels(labelEventMappingConverter.convertDomain(mappings));
    }
}