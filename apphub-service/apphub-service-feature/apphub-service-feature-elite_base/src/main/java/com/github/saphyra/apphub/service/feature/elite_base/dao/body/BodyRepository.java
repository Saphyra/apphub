package com.github.saphyra.apphub.service.feature.elite_base.dao.body;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

interface BodyRepository extends CrudRepository<BodyEntity, String> {
    Optional<BodyEntity> findByStarSystemIdAndBodyId(String starSystemId, Long bodyId);

    Optional<BodyEntity> findByBodyName(String bodyName);

    //TODO unit test
    //TODO index
    List<BodyEntity> getByStarSystemId(String starSystemId);
}
