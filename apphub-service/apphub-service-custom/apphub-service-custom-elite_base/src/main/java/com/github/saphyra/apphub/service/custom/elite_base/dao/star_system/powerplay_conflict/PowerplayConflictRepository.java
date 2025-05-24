package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.powerplay_conflict;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface PowerplayConflictRepository extends CrudRepository<PowerplayConflictEntity, PowerplayConflictEntityId> {
    List<PowerplayConflictEntity> getByIdStarSystemId(String starSystemId);
}
