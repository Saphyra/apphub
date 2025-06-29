package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.power;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface StarSystemPowerMappingRepository extends CrudRepository<StarSystemPowerMappingEntity, StarSystemPowerMappingEntity> {
    List<StarSystemPowerMappingEntity> getByStarSystemId(String starSystemId);
}
