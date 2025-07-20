package com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.dao;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.common_util.dao.AbstractDao;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
//TODO unit test
public class EventLabelMappingDao extends AbstractDao<EventLabelMappingEntity, EventLabelMapping, EventLabelMappingEntity, EventLabelMappingRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    EventLabelMappingDao(EventLabelMappingConverter converter, EventLabelMappingRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }

    public void deleteByUserIdAndEventId(UUID userId, UUID eventId) {
        repository.deleteByUserIdAndEventId(uuidConverter.convertDomain(userId), uuidConverter.convertDomain(eventId));
    }

    public List<EventLabelMapping> getByEventId(UUID eventId) {
        return converter.convertEntity(repository.getByEventId(uuidConverter.convertDomain(eventId)));
    }

    public void deleteByUserIdAndLabelId(UUID userId, UUID labelId) {
        repository.deleteByUserIdAndLabelId(uuidConverter.convertDomain(userId), uuidConverter.convertDomain(labelId));
    }
}
