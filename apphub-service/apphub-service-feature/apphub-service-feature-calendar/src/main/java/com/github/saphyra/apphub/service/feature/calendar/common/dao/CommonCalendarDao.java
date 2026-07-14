package com.github.saphyra.apphub.service.feature.calendar.common.dao;

import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.Label;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.LabelDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.OccurrenceDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class CommonCalendarDao implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;
    private final CommonCalendarRepository repository;
    @Getter
    private final EventDao eventDao;
    @Getter
    private final OccurrenceDao occurrenceDao;
    @Getter
    private final EventLabelMappingDao eventLabelMappingDao;
    @Getter
    private final LabelDao labelDao;
    private final AlmDao almDao;

    @Override
    public void deleteByUserId(UUID userId) {
        List<UUID> eventIds = eventDao.getByUserId(userId)
            .stream()
            .map(Event::getEventId)
            .toList();
        eventIds.forEach(occurrenceDao::deleteByEventId);
        eventDao.delete(userId, eventIds);

        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }

    /**
     * Saves a new event and its data
     *
     * @param event       to save
     * @param occurrences of the event
     * @param labelIds    all labelIds of the event
     */
    public void saveNewEvent(Event event, List<Occurrence> occurrences, List<UUID> labelIds) {
        eventDao.save(event);
        occurrenceDao.save(occurrences);

        editLabelsOfEvent(event.getUserId(), event.getEventId(), labelIds);
    }

    /**
     * <ul>
     *     <li>Delete the events</li>
     *     <li>Delete all occurrences of events</li>
     *     <li>Delete eventId-labelIds mappings</li>
     *     <li>Remove eventIds from all labelId->eventIds mappings</li>
     * </ul>
     */
    public void deleteEvents(UUID userId, List<UUID> eventIds) {
        eventDao.delete(userId, eventIds);

        List<Occurrence> occurrencesToDelete = eventIds.stream()
            .flatMap(eventId -> occurrenceDao.getByEventId(eventId).stream())
            .toList();
        occurrenceDao.delete(occurrencesToDelete);

        eventLabelMappingDao.deleteByEventId(userId, eventIds);
    }

    /**
     * <ul>
     *     <li>Delete labelId-eventIds mapping</li>
     *     <li>Remove labelId from all eventId-labelId mappings</li>
     *     <li>Delete Alms of the given label</li>
     * </ul>
     */
    public void deleteLabel(UUID userId, UUID labelId) {
        labelDao.delete(userId, labelId);
        eventLabelMappingDao.deleteByLabelId(userId, labelId);
        almDao.deleteByObject(labelId, SharedObjectType.LABEL);
    }

    public void saveLabel(Label label) {
        labelDao.save(label);
        eventLabelMappingDao.saveEventsOfLabel(label.getUserId(), label.getLabelId(), List.of());
    }

    /**
     * <ul>
     *     <li>Save the list of labels for the given event</li>
     *     <li>Adds eventId to the event list of each label</li>
     *     <li>Deletes eventId from labels no longer mapped to this event</li>
     * </ul>
     */
    public void editLabelsOfEvent(UUID userId, UUID eventId, List<UUID> labelIds) {
        eventLabelMappingDao.saveLabelsOfEvent(userId, eventId, labelIds);

        List<UUID> labelsOfUser = labelDao.getByUserId(userId)
            .stream()
            .map(Label::getLabelId)
            .toList();

        List<BiWrapper<UUID, List<UUID>>> modifiedMappings = eventLabelMappingDao.getEventsOfLabels(userId, labelsOfUser)
            .stream()
            .map(mapping -> { //BiWrapper<LabelId, List<EventId>
                UUID labelId = mapping.getEntity1();
                List<UUID> eventIds = mapping.getEntity2();
                if (labelIds.contains(labelId) && !eventIds.contains(eventId)) {
                    return Optional.of(new BiWrapper<>(
                        labelId,
                        Stream.concat(eventIds.stream(), Stream.of(eventId)) //Add event to existing list
                            .toList()
                    ));
                }

                if (!labelIds.contains(labelId) && eventIds.contains(eventId)) { //Label is not in new label list of event, but eventId is in the event list of label
                    return Optional.of(new BiWrapper<>(
                        labelId,
                        eventIds.stream().filter(e -> !e.equals(eventId)) //Remove the label
                            .toList()
                    ));
                }

                //No modification needed
                return Optional.<BiWrapper<UUID, List<UUID>>>empty();
            })
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();

        if (!modifiedMappings.isEmpty()) {
            eventLabelMappingDao.saveEventsOfLabels(userId, modifiedMappings);
        }
    }
}
