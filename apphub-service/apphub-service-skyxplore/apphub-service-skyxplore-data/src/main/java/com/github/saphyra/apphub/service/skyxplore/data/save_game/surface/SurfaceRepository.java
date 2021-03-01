package com.github.saphyra.apphub.service.skyxplore.data.save_game.surface;

import org.springframework.data.repository.CrudRepository;

//TODO unit test - Check if coordinate stored and deleted properly
interface SurfaceRepository extends CrudRepository<SurfaceEntity, String> {
    void deleteByGameId(String gameId);
}
