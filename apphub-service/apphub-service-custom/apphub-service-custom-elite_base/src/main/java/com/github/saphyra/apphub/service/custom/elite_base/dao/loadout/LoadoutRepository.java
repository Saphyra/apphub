package com.github.saphyra.apphub.service.custom.elite_base.dao.loadout;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface LoadoutRepository extends CrudRepository<LoadoutEntity, LoadoutEntityId> {
    @Query("SELECT e FROM LoadoutEntity e WHERE (e.externalReference=:externalReference OR e.marketId=:marketId) AND e.type=:loadoutType")
    List<LoadoutEntity> getByExternalReferenceOrMarketIdAndLoadoutType(@Param("externalReference") String externalReference, @Param("marketId") Long marketId, @Param("loadoutType") LoadoutType loadoutType);

    @Modifying
    @Query("DELETE FROM LoadoutEntity e WHERE e.externalReference=:externalReference AND e.type=:loadoutType AND e.name IN :names")
    @Transactional
    void deleteByExternalReferenceAndLoadoutTypeAndNameIn(
        @Param("externalReference") String externalReference,
        @Param("loadoutType") LoadoutType loadoutType,
        @Param("names") List<String> names
    );
}
