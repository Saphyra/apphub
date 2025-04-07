package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.powerplay_conflict;

import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class PowerplayConflictDao extends AbstractDao<PowerplayConflictEntity, PowerplayConflict, PowerplayConflictEntityId, PowerplayConflictRepository> {
    private final UuidConverter uuidConverter;

    PowerplayConflictDao(PowerplayConflictConverter converter, PowerplayConflictRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public List<PowerplayConflict> getByStarSystemId(UUID starSystemId) {
        return converter.convertEntity(repository.getByIdStarSystemId(uuidConverter.convertDomain(starSystemId)));
    }
}
