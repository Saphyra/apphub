package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.star_system_data.conflict;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface MinorFactionConflictRepository extends CrudRepository<MinorFactionConflictEntity, String> {
    List<MinorFactionConflictEntity> getByStarSystemId(String starSystemId);
}
