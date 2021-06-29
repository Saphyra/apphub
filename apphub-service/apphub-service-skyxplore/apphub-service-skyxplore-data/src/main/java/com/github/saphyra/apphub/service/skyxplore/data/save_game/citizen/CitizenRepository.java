package com.github.saphyra.apphub.service.skyxplore.data.save_game.citizen;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

interface CitizenRepository extends CrudRepository<CitizenEntity, String> {
    @Transactional
    @Modifying
    @Query("DELETE FROM CitizenEntity e WHERE e.gameId = :gameId")
    void deleteByGameId(@Param("gameId") String gameId);
}
