package com.github.saphyra.apphub.service.feature.elite_base.processor.dao.body.body_material;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface BodyMaterialRepository extends CrudRepository<BodyMaterialEntity, BodyMaterialEntityId> {
    @Query("SELECT e FROM BodyMaterialEntity e WHERE e.id.bodyId=:bodyId")
    List<BodyMaterialEntity> getByBodyId(@Param("bodyId") String bodyId);
}
