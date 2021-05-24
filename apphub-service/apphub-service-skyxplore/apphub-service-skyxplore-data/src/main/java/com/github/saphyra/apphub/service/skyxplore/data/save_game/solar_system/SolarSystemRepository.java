package com.github.saphyra.apphub.service.skyxplore.data.save_game.solar_system;

import org.springframework.data.repository.CrudRepository;

interface SolarSystemRepository extends CrudRepository<SolarSystemEntity, String> {
    void deleteByGameId(String gameId);
}
