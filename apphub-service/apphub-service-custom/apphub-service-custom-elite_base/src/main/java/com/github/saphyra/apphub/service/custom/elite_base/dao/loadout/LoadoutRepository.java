package com.github.saphyra.apphub.service.custom.elite_base.dao.loadout;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface LoadoutRepository extends CrudRepository<LoadoutEntity, LoadoutEntityId> {
    @Query("SELECT e FROM LoadoutEntity e WHERE (e.externalReference=:externalReference OR e.marketId=:marketId) AND e.type=:loadoutType")
    List<LoadoutEntity> getByExternalReferenceOrMarketIdAndLoadoutType(@Param("externalReference") String externalReference, @Param("marketId") Long marketId, @Param("loadoutType") LoadoutType loadoutType);
}
