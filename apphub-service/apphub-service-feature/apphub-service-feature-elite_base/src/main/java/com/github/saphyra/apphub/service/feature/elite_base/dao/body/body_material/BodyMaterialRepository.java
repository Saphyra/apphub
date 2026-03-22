package com.github.saphyra.apphub.service.feature.elite_base.dao.body.body_material;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface BodyMaterialRepository extends CrudRepository<BodyMaterialEntity, String> {
    List<BodyMaterialEntity> getByBodyId(String bodyId);
}
