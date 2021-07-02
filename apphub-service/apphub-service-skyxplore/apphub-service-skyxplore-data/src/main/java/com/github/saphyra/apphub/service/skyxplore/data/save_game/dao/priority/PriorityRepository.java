package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.priority;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

interface PriorityRepository extends CrudRepository<PriorityEntity, PriorityPk> {
    @Transactional
    @Modifying
    @Query("DELETE FROM PriorityEntity e WHERE e.gameId = :gameId")
    void deleteByGameId(@Param("gameId") String gameId);
}
