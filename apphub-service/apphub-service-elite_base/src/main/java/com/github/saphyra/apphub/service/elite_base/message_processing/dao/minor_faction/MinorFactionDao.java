package com.github.saphyra.apphub.service.elite_base.message_processing.dao.minor_faction;

import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
//TODO unit test
public class MinorFactionDao extends AbstractDao<MinorFactionEntity, MinorFaction, String, MinorFactionRepository> {
    public MinorFactionDao(MinorFactionConverter converter, MinorFactionRepository repository) {
        super(converter, repository);
    }

    public Optional<MinorFaction> findByFactionName(String factionName) {
        return converter.convertEntity(repository.findByFactionName(factionName));
    }
}
