package com.github.saphyra.apphub.service.skyxplore.data.save_game.surface;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

interface SurfaceRepository extends CrudRepository<SurfaceEntity, String> {
    @Transactional
    @Modifying
    @Query("DELETE FROM SurfaceEntity e WHERE e.gameId = :gameId")
    void deleteByGameId(@Param("gameId") String gameId);
}
