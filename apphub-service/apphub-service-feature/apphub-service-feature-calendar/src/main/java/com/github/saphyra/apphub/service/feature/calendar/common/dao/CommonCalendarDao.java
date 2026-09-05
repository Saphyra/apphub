package com.github.saphyra.apphub.service.feature.calendar.common.dao;

import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMapping;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.LabelEventMapping;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.Label;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.LabelDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.service.LabelObjectQueryService;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.OccurrenceDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
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
    private final LabelObjectQueryService labelObjectQueryService;

    @Override
    public void deleteByUserId(UUID userId) {
        List<UUID> labelIds = labelDao.getByUserId(userId)
            .stream()
            .map(Label::getLabelId)
            .toList();
        List<UUID> eventIds = eventDao.getByUserId(userId)
            .stream()
            .map(Event::getEventId)
            .toList();

        deleteEvents(userId, eventIds);
        labelIds.forEach(labelId -> almDao.deleteByObject(labelId, SharedObjectType.LABEL));

        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }

    /**
     * Saves a new event and its data
     *
     * @param event       to save
     * @param occurrences of the event
     * @param labelIds    all labelIds of the event
     */
    public void saveNewEvent(Event event, List<Occurrence> occurrences, Map<UUID, UUID> labelIds) {
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
        occurrencesToDelete.stream()
            .map(Occurrence::getOccurrenceId)
            .forEach(occurrenceId -> almDao.deleteByObject(occurrenceId, SharedObjectType.OCCURRENCE));

        eventIds.forEach(eventId -> almDao.deleteByObject(eventId, SharedObjectType.EVENT));

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
        log.info("Deleting label {} of user {}.", labelId, userId);
        labelDao.delete(userId, labelId);
        eventLabelMappingDao.deleteByLabelId(userId, labelId);
        almDao.deleteByObject(labelId, SharedObjectType.LABEL);
    }

    public void saveLabel(Label label) {
        labelDao.save(label);

        LabelEventMapping mapping = new LabelEventMapping(label.getUserId(), label.getLabelId(), Map.of());

        eventLabelMappingDao.saveEventsOfLabel(mapping);
    }

    /**
     * <ul>
     *     <li>Save the list of labels for the given event</li>
     *     <li>Adds eventId to the event list of each label</li>
     *     <li>Deletes eventId from labels no longer mapped to this event</li>
     * </ul>
     */
    public void editLabelsOfEvent(UUID userId, UUID eventId, Map<UUID, UUID> labelIds) {
        EventLabelMapping newMapping = new EventLabelMapping(userId, eventId, labelIds);

        eventLabelMappingDao.saveLabelsOfEvent(newMapping);

        //Get labels available for user
        List<LabelEventMapping> modifiedMappings = labelObjectQueryService.getByUserId(userId)
            .map(BiWrapper::getEntity1)
            .map(label -> new BiWrapper<>(label.getUserId(), label.getLabelId()))
            .map(bw -> eventLabelMappingDao.getEventsOfLabel(bw.getEntity1(), bw.getEntity2()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            //Iterate through labels to see which one needs to be changed
            .map(mapping -> {
                //Label is added to event, add event to LabelEventMapping
                if (labelIds.containsKey(mapping.getLabelId()) && !mapping.getEventIds().containsKey(eventId)) {
                    mapping.addEvent(userId, eventId);

                    return Optional.of(mapping);
                }

                //Label is removed from event, remove event from LabelEventMapping
                UUID labelId = mapping.getLabelId();
                Map<UUID, UUID> eventIds = mapping.getEventIds();
                if (!labelIds.containsKey(mapping.getLabelId()) && mapping.getEventIds().containsKey(eventId)) {
                    mapping.removeEvent(eventId);
                    return Optional.of(mapping);
                }

                //No modification needed
                return Optional.<LabelEventMapping>empty();
            })
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();

        if (!modifiedMappings.isEmpty()) {
            eventLabelMappingDao.saveEventsOfLabels(modifiedMappings);
        }
    }
}
