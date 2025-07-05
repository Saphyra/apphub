package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.conflict;

import com.github.saphyra.apphub.lib.common_util.dao.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class MinorFactionConflictDao extends AbstractDao<MinorFactionConflictEntity, MinorFactionConflict, String, MinorFactionConflictRepository> {
    private final UuidConverter uuidConverter;

    MinorFactionConflictDao(MinorFactionConflictConverter converter, MinorFactionConflictRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public List<MinorFactionConflict> getByStarSystemId(UUID starSystemId) {
        return converter.convertEntity(repository.getByStarSystemId(uuidConverter.convertDomain(starSystemId)));
    }

    public void deleteById(UUID id) {
        deleteById(uuidConverter.convertDomain(id));
    }
}
