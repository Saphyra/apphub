package com.github.saphyra.apphub.service.feature.elite_base.dao.body;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

interface BodyRepository extends CrudRepository<BodyEntity, String> {
    Optional<BodyEntity> findByBodyName(String bodyName);

    List<BodyEntity> getByStarSystemIdIn(List<String> starSystemIds);
}
