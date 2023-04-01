package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.solar_system;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface SolarSystemRepository extends CrudRepository<SolarSystemEntity, String> {
    @Modifying
    @Query("DELETE FROM SolarSystemEntity e WHERE e.gameId = :gameId")
    void deleteByGameId(@Param("gameId") String gameId);

    List<SolarSystemEntity> getByGameId(String gameId, PageRequest pageRequest);
}
