package com.github.saphyra.apphub.service.elite_base.dao.star_system_data.power;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

//TODO unit test
interface StarSystemPowerMappingRepository extends CrudRepository<StarSystemPowerMappingEntity, StarSystemPowerMappingEntity> {
    List<StarSystemPowerMappingEntity> getByStarSystemId(String starSystemId);
}
