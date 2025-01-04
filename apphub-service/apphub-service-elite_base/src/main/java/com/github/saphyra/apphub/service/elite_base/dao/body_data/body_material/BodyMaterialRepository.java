package com.github.saphyra.apphub.service.elite_base.dao.body_data.body_material;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

//TODO unit test
interface BodyMaterialRepository extends CrudRepository<BodyMaterialEntity, String> {
    List<BodyMaterialEntity> getByBodyId(String bodyId);
}
