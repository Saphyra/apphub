package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.star_system_data;

import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class StarSystemDataDao extends AbstractDao<StarSystemDataEntity, StarSystemData, String, StarSystemDataRepository> {
    private final UuidConverter uuidConverter;

    StarSystemDataDao(StarSystemDataConverter converter, StarSystemDataRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public Optional<StarSystemData> findById(UUID starSystemId) {
        return findById(uuidConverter.convertDomain(starSystemId));
    }
}
