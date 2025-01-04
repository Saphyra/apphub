package com.github.saphyra.apphub.service.elite_base.dao.star_system_data.conflict.minor_faction;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

//TODO unit test
interface ConflictingMinorFactionRepository extends CrudRepository<ConflictingMinorFactionEntity, ConflictingMinorFactionEntityId> {
    List<ConflictingMinorFactionEntity> getByConflictId(String conflictId);

    void deleteByConflictId(String conflictId);
}
