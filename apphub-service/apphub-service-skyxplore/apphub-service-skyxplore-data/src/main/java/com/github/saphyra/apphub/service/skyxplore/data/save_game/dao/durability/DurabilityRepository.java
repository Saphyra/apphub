package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.durability;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

interface DurabilityRepository extends CrudRepository<DurabilityEntity, String> {
    @Modifying
    @Query("DELETE FROM DurabilityEntity e WHERE e.gameId = :gameId")
    void deleteByGameId(@Param("gameId") String gameId);
}
