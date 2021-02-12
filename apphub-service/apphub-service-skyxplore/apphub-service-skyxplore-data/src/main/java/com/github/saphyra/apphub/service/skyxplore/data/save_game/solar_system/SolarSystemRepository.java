package com.github.saphyra.apphub.service.skyxplore.data.save_game.solar_system;

import org.springframework.data.repository.CrudRepository;

//TODO unit test - Check if coordinate stored and deleted properly
interface SolarSystemRepository extends CrudRepository<SolarSystemEntity, String> {
    void deleteByGameId(String gameId);
}
