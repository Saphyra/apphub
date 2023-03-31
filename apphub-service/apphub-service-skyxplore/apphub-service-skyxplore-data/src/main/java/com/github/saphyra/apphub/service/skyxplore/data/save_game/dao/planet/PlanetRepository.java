package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.planet;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface PlanetRepository extends JpaRepository<PlanetEntity, String> {
    @Modifying
    @Query("DELETE FROM PlanetEntity e WHERE e.gameId = :gameId")
    void deleteByGameId(@Param("gameId") String gameId);

    List<PlanetEntity> getBySolarSystemId(String solarSystemId);

    //TODO unit test
    List<PlanetEntity> getByGameId(String gameId, PageRequest pageRequest);
}
