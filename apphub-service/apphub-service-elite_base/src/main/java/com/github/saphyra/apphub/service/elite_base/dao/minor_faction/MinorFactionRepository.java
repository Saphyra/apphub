package com.github.saphyra.apphub.service.elite_base.dao.minor_faction;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

//TODO unit test
interface MinorFactionRepository extends CrudRepository<MinorFactionEntity, String> {
    Optional<MinorFactionEntity> findByFactionName(String factionName);
}
