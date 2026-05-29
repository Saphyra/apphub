package com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.deprecated_dao;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.common_util.dao.AbstractDao;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Deprecated(forRemoval = true)
public class DeprecatedEventLabelMappingDao extends AbstractDao<DeprecatedEventLabelMappingEntity, DeprecatedEventLabelMapping, String, DeprecatedEventLabelMappingRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    DeprecatedEventLabelMappingDao(DeprecatedEventLabelMappingConverter converter, DeprecatedEventLabelMappingRepository repository, UuidConverter uuidConverter) {
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

    public List<DeprecatedEventLabelMapping> getByEventId(UUID eventId) {
        return converter.convertEntity(repository.getByEventId(uuidConverter.convertDomain(eventId)));
    }

    public void deleteByUserIdAndLabelId(UUID userId, UUID labelId) {
        repository.deleteByUserIdAndLabelId(uuidConverter.convertDomain(userId), uuidConverter.convertDomain(labelId));
    }

    public List<DeprecatedEventLabelMapping> getByUserIdAndLabelId(UUID userId, UUID labelId) {
        return converter.convertEntity(repository.getByUserIdAndLabelId(uuidConverter.convertDomain(userId), uuidConverter.convertDomain(labelId)));
    }
}
