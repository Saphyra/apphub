package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.process;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface ProcessRepository extends CrudRepository<ProcessEntity, String> {
    @Modifying
    @Query("DELETE FROM ProcessEntity e WHERE e.gameId = :gameId")
    void deleteByGameId(@Param("gameId") String gameId);

    List<ProcessEntity> getByGameId(String gameId);
}
