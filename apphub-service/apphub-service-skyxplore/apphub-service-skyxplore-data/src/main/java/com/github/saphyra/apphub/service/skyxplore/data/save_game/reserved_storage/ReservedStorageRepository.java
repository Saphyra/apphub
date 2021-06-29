package com.github.saphyra.apphub.service.skyxplore.data.save_game.reserved_storage;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

interface ReservedStorageRepository extends CrudRepository<ReservedStorageEntity, String> {
    @Transactional
    @Modifying
    @Query("DELETE FROM ReservedStorageEntity e WHERE e.gameId = :gameId")
    void deleteByGameId(@Param("gameId") String gameId);
}
