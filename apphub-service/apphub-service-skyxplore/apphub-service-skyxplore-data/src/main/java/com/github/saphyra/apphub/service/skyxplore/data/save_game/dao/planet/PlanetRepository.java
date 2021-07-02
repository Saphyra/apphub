package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.planet;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

interface PlanetRepository extends CrudRepository<PlanetEntity, String> {
    @Transactional
    void deleteByGameId(@Param("gameId") String gameId);
}
