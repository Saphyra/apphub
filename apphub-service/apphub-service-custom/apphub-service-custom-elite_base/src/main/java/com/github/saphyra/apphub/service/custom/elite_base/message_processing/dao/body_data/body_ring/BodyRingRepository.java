package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.body_data.body_ring;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface BodyRingRepository extends CrudRepository<BodyRingEntity, String> {
    List<BodyRingEntity> getByBodyId(String bodyId);
}
