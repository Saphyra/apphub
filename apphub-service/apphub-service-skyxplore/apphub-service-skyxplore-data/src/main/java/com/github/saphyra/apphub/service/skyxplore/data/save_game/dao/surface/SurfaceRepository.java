package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.surface;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface SurfaceRepository extends CrudRepository<SurfaceEntity, String> {
    @Modifying
    @Query("DELETE FROM SurfaceEntity e WHERE e.gameId = :gameId")
    void deleteByGameId(@Param("gameId") String gameId);

    List<SurfaceEntity> getByPlanetId(String planetId);

    //TODO unit test
    List<SurfaceEntity> getByGameId(String gameId, PageRequest pageRequest);
}
