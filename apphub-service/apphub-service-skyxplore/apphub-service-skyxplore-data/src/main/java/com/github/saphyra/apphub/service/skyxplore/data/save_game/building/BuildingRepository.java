package com.github.saphyra.apphub.service.skyxplore.data.save_game.building;

import org.springframework.data.repository.CrudRepository;

interface BuildingRepository extends CrudRepository<BuildingEntity, String> {
    void deleteByGameId(String gameId);
}