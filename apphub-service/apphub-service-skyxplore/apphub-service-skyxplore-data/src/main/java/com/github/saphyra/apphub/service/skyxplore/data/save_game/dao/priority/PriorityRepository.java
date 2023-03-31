package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.priority;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface PriorityRepository extends JpaRepository<PriorityEntity, PriorityPk> {
    @Modifying
    @Query("DELETE FROM PriorityEntity e WHERE e.gameId = :gameId")
    void deleteByGameId(@Param("gameId") String gameId);

    List<PriorityEntity> getByPkLocation(String location);

    //TODO unit test
    List<PriorityEntity> getByGameId(String gameId, PageRequest pageRequest);
}
