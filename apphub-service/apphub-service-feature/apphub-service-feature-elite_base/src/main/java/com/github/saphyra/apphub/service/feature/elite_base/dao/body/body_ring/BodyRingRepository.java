package com.github.saphyra.apphub.service.feature.elite_base.dao.body.body_ring;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface BodyRingRepository extends CrudRepository<BodyRingEntity, String> {
    List<BodyRingEntity> getByBodyId(String bodyId);

    //TODO unit test
    List<BodyRingEntity> getByBodyIdIn(List<String> bodyIds);
}
