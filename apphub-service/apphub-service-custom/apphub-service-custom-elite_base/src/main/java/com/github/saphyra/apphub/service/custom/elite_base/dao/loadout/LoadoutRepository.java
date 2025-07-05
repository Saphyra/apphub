package com.github.saphyra.apphub.service.custom.elite_base.dao.loadout;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface LoadoutRepository extends CrudRepository<LoadoutEntity, LoadoutEntityId> {
    List<LoadoutEntity> getByMarketIdAndType(Long marketId, LoadoutType type);
}
