package com.github.saphyra.apphub.service.skyxplore.data.save_game.planet;

import org.springframework.data.repository.CrudRepository;

interface PlanetRepository extends CrudRepository<PlanetEntity, String> {
    void deleteByGameId(String gameId);
}
