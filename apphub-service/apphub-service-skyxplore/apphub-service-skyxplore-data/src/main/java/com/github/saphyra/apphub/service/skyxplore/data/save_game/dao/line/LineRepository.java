package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.line;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface LineRepository extends CrudRepository<LineEntity, String> {
    @Modifying
    @Query("DELETE FROM LineEntity e WHERE e.gameId = :gameId")
    void deleteByGameId(@Param("gameId") String gameId);

    List<LineEntity> getByReferenceId(String referenceId);
}
