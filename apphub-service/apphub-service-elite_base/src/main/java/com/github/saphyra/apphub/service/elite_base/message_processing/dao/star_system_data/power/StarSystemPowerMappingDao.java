package com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system_data.power;

import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class StarSystemPowerMappingDao extends AbstractDao<StarSystemPowerMappingEntity, StarSystemPowerMapping, StarSystemPowerMappingEntity, StarSystemPowerMappingRepository> {
    private final UuidConverter uuidConverter;

    StarSystemPowerMappingDao(StarSystemPowerMappingConverter converter, StarSystemPowerMappingRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public List<StarSystemPowerMapping> getByStarSystemId(UUID starSystemId) {
        return converter.convertEntity(repository.getByStarSystemId(uuidConverter.convertDomain(starSystemId)));
    }
}
