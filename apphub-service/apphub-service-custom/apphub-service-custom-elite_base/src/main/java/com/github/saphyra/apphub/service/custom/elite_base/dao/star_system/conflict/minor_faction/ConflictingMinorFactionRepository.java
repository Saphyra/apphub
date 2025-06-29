package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.conflict.minor_faction;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface ConflictingMinorFactionRepository extends CrudRepository<ConflictingMinorFactionEntity, ConflictingMinorFactionEntityId> {
    List<ConflictingMinorFactionEntity> getByConflictId(String conflictId);

    void deleteByConflictId(String conflictId);
}
