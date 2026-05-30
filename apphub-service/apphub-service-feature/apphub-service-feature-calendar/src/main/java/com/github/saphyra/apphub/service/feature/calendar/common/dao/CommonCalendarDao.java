package com.github.saphyra.apphub.service.feature.calendar.common.dao;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
//TODO unit test
public class CommonCalendarDao implements DeleteByUserIdDao {
    @Override
    public void deleteByUserId(UUID userId) {

    }

    /**
     * Saves a new event and its data
     *
     * @param event       to save
     * @param occurrences of the event
     * @param labelIds    all labelIds of the event
     */
    public void saveNewEvent(Event event, List<Occurrence> occurrences, List<UUID> labelIds) {

    }

    /**
     * <ul>
     *     <li>Delete the events</li>
     *     <li>Delete all occurrences of events</li>
     *     <li>Delete eventId-labelIds mappings</li>
     *     <li>Remove eventId from all labelId->eventIds mappings</li>
     * </ul>
     */
    public void deleteEvent(UUID userId, List<UUID> eventId) {

    }

    /**
     * <ul>
     *     <li>Delete labelId-eventIds mapping</li>
     *     <li>Remove labelId from all eventId-labelId mappings</li>
     * </ul>
     */
    public void deleteLabel(UUID userId, UUID labelId) {

    }
}
