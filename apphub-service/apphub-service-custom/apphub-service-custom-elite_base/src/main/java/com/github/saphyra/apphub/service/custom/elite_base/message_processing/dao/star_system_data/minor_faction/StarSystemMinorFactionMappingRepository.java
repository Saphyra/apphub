package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.star_system_data.minor_faction;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface StarSystemMinorFactionMappingRepository extends CrudRepository<StarSystemMinorFactionMappingEntity, StarSystemMinorFactionMappingEntity> {
    List<StarSystemMinorFactionMappingEntity> getByStarSystemId(String starSystemId);
}
