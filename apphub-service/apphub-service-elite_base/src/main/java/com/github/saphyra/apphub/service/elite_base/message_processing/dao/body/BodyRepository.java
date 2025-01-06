package com.github.saphyra.apphub.service.elite_base.message_processing.dao.body;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

//TODO unit test
interface BodyRepository extends CrudRepository<BodyEntity, String> {
    Optional<BodyEntity> findByStarSystemIdAndBodyId(String starSystemId, Long bodyId);

    Optional<BodyEntity> findByBodyName(String bodyName);
}
