package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.star_system_data.minor_faction;

import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class StarSystemMinorFactionMappingDao extends AbstractDao<StarSystemMinorFactionMappingEntity, StarSystemMinorFactionMapping, StarSystemMinorFactionMappingEntity, StarSystemMinorFactionMappingRepository> {
    private final UuidConverter uuidConverter;

    StarSystemMinorFactionMappingDao(StarSystemMinorFactionMappingConverter converter, StarSystemMinorFactionMappingRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public List<StarSystemMinorFactionMapping> getByStarSystemId(UUID starSystemId) {
        return converter.convertEntity(repository.getByStarSystemId(uuidConverter.convertDomain(starSystemId)));
    }
}
