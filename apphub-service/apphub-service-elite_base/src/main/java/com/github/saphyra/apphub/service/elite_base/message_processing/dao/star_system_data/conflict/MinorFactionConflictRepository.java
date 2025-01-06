package com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system_data.conflict;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

//TODO unit test
interface MinorFactionConflictRepository extends CrudRepository<MinorFactionConflictEntity, String> {
    List<MinorFactionConflictEntity> getByStarSystemId(String starSystemId);
}
