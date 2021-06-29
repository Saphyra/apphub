package com.github.saphyra.apphub.service.skyxplore.data.save_game.solar_system;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

interface SolarSystemRepository extends CrudRepository<SolarSystemEntity, String> {
    @Transactional
    @Modifying
    @Query("DELETE FROM SolarSystemEntity e WHERE e.gameId = :gameId")
    void deleteByGameId(@Param("gameId") String gameId);
}
