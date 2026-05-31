package com.github.saphyra.apphub.service.feature.calendar.common.dao;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.LabelDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.OccurrenceDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
//TODO unit test
public class CommonCalendarDao implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;
    private final CommonCalendarRepository repository;
    private final EventDao eventDao;
    private final OccurrenceDao occurrenceDao;
    private final EventLabelMappingDao eventLabelMappingDao;
    private final LabelDao labelDao;

    @Override
    public void deleteByUserId(UUID userId) {
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
        occurrenceDao.save(event.getUserId(), occurrences);

        eventLabelMappingDao.saveLabelsOfEvent(event.getUserId(), event.getEventId(), labelIds);
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
            .flatMap(eventId -> occurrenceDao.getByEventId(userId, eventId).stream())
            .toList();
        occurrenceDao.delete(userId, occurrencesToDelete);

        eventLabelMappingDao.deleteByEventId(userId, eventIds);
    }

    /**
     * <ul>
     *     <li>Delete labelId-eventIds mapping</li>
     *     <li>Remove labelId from all eventId-labelId mappings</li>
     * </ul>
     */
    public void deleteLabel(UUID userId, UUID labelId) {
        labelDao.delete(userId, labelId);
        eventLabelMappingDao.deleteByLabelId(userId, labelId);
    }
}
