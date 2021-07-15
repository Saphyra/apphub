package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.planet;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface PlanetRepository extends CrudRepository<PlanetEntity, String> {
    @Modifying
    @Query("DELETE FROM PlanetEntity e WHERE e.gameId = :gameId")
    void deleteByGameId(@Param("gameId") String gameId);

    List<PlanetEntity> getBySolarSystemId(String solarSystemId);
}
