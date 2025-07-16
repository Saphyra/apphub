package com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.dao;

import com.github.saphyra.apphub.lib.common_util.dao.AbstractDao;
import org.springframework.stereotype.Component;

@Component
public class EventLabelMappingDao extends AbstractDao<EventLabelMappingEntity, EventLabelMapping, EventLabelMappingEntity, EventLabelMappingRepository> {
    protected EventLabelMappingDao(EventLabelMappingConverter converter, EventLabelMappingRepository repository) {
        super(converter, repository);
    }
}
