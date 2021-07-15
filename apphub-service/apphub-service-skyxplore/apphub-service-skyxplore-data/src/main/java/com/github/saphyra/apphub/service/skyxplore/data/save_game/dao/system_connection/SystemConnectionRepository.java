package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.system_connection;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface SystemConnectionRepository extends CrudRepository<SystemConnectionEntity, String> {
    @Modifying
    @Query("DELETE FROM SystemConnectionEntity e WHERE e.gameId = :gameId")
    void deleteByGameId(@Param("gameId") String gameId);

    List<SystemConnectionEntity> getByGameId(String gameId);
}
