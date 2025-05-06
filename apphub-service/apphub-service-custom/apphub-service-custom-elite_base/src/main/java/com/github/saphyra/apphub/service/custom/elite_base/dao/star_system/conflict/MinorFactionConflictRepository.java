package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.conflict;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface MinorFactionConflictRepository extends CrudRepository<MinorFactionConflictEntity, String> {
    List<MinorFactionConflictEntity> getByStarSystemId(String starSystemId);
}
