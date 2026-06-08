package com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.star_system_data;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface StarSystemDataRepository extends CrudRepository<StarSystemDataEntity, String> {
    //TODO unit test
    //TODO index
    List<StarSystemDataEntity> getByControllingPower(Power power);
}
