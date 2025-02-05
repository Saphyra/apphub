package com.github.saphyra.apphub.service.elite_base.message_processing.dao.body_data.body_material;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface BodyMaterialRepository extends CrudRepository<BodyMaterialEntity, String> {
    List<BodyMaterialEntity> getByBodyId(String bodyId);
}
