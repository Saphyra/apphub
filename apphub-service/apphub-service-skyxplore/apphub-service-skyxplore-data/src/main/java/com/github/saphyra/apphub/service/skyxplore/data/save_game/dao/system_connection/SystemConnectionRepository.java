package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.system_connection;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

interface SystemConnectionRepository extends CrudRepository<SystemConnectionEntity, String> {
    @Transactional
    void deleteByGameId(@Param("gameId") String gameId);
}
