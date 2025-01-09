package com.github.saphyra.apphub.service.elite_base.message_processing.dao.minor_faction;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

interface MinorFactionRepository extends CrudRepository<MinorFactionEntity, String> {
    Optional<MinorFactionEntity> findByFactionName(String factionName);
}
