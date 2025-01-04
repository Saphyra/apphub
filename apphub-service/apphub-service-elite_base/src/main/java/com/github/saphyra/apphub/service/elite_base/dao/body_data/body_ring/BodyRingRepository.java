package com.github.saphyra.apphub.service.elite_base.dao.body_data.body_ring;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

//TODO unit test
interface BodyRingRepository extends CrudRepository<BodyRingEntity, String> {
    List<BodyRingEntity> getByBodyId(String bodyId);
}
