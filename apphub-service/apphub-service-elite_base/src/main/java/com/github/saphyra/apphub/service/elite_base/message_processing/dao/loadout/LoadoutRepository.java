package com.github.saphyra.apphub.service.elite_base.message_processing.dao.loadout;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

//TODO unit test
interface LoadoutRepository extends CrudRepository<LoadoutEntity, LoadoutEntityId> {
    List<LoadoutEntity> getByExternalReferenceOrMarketId(String externalReference, Long marketId);
}
