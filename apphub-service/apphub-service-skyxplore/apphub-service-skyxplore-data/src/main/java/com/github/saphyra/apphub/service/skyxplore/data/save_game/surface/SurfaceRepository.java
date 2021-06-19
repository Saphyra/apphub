package com.github.saphyra.apphub.service.skyxplore.data.save_game.surface;

import org.springframework.data.repository.CrudRepository;

interface SurfaceRepository extends CrudRepository<SurfaceEntity, String> {
    void deleteByGameId(String gameId);
}
