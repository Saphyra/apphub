package com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system_data.conflict.minor_faction;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface ConflictingMinorFactionRepository extends CrudRepository<ConflictingMinorFactionEntity, ConflictingMinorFactionEntityId> {
    List<ConflictingMinorFactionEntity> getByConflictId(String conflictId);

    void deleteByConflictId(String conflictId);
}
