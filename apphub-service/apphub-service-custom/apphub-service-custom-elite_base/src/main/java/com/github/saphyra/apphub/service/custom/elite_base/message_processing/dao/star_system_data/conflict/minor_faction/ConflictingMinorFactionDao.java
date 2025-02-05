package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.star_system_data.conflict.minor_faction;

import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ConflictingMinorFactionDao extends AbstractDao<ConflictingMinorFactionEntity, ConflictingMinorFaction, ConflictingMinorFactionEntityId, ConflictingMinorFactionRepository> {
    private final UuidConverter uuidConverter;

    ConflictingMinorFactionDao(ConflictingMinorFactionConverter converter, ConflictingMinorFactionRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public List<ConflictingMinorFaction> getByConflictId(UUID conflictId) {
        return converter.convertEntity(repository.getByConflictId(uuidConverter.convertDomain(conflictId)));
    }

    public void deleteByConflictId(UUID conflictId) {
        repository.deleteByConflictId(uuidConverter.convertDomain(conflictId));
    }
}
