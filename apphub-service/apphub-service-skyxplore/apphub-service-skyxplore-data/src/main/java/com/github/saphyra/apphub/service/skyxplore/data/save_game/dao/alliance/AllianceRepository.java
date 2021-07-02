package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.alliance;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

interface AllianceRepository extends CrudRepository<AllianceEntity, String> {
    @Transactional
    @Modifying
    @Query("DELETE FROM AllianceEntity e WHERE e.gameId = :gameId")
    void deleteByGameId(@Param("gameId") String gameId);

    List<AllianceEntity> getByGameId(String gameId);
}
