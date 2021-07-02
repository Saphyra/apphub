package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.solar_system;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

interface SolarSystemRepository extends CrudRepository<SolarSystemEntity, String> {
    @Transactional
    void deleteByGameId(@Param("gameId") String gameId);
}
