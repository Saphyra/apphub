package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.minor_faction;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface StarSystemMinorFactionMappingRepository extends CrudRepository<StarSystemMinorFactionMappingEntity, StarSystemMinorFactionMappingEntity> {
    List<StarSystemMinorFactionMappingEntity> getByStarSystemId(String starSystemId);
}
