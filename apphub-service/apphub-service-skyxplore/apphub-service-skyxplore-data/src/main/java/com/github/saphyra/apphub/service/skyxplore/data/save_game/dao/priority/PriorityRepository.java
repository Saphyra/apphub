package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.priority;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface PriorityRepository extends JpaRepository<PriorityEntity, String> {
    @Modifying
    @Query("DELETE FROM PriorityEntity e WHERE e.gameId = :gameId")
    void deleteByGameId(@Param("gameId") String gameId);

    List<PriorityEntity> getByLocation(String location);

    List<PriorityEntity> getByGameId(String gameId, PageRequest pageRequest);
}
