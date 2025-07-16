package com.github.saphyra.apphub.service.calendar.domain.event.dao;

import com.github.saphyra.apphub.lib.common_util.dao.AbstractDao;
import org.springframework.stereotype.Component;

@Component
public class EventDao extends AbstractDao<EventEntity, Event, String, EventRepository> {
    EventDao(EventConverter converter, EventRepository repository) {
        super(converter, repository);
    }
}
