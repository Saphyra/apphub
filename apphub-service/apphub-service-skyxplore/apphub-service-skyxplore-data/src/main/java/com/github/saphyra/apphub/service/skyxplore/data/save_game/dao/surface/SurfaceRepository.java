package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.surface;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

interface SurfaceRepository extends CrudRepository<SurfaceEntity, String> {
    @Transactional
    void deleteByGameId(@Param("gameId") String gameId);
}
