package com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction;

import com.github.saphyra.apphub.lib.common_util.dao.AbstractDao;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MinorFactionDao extends AbstractDao<MinorFactionEntity, MinorFaction, String, MinorFactionRepository> {
    MinorFactionDao(MinorFactionConverter converter, MinorFactionRepository repository) {
        super(converter, repository);
    }

    public Optional<MinorFaction> findByFactionName(String factionName) {
        return converter.convertEntity(repository.findByFactionName(factionName));
    }
}
