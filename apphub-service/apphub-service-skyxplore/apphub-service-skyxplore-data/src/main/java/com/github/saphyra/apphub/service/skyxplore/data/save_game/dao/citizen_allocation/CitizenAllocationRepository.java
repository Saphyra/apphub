package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.citizen_allocation;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface CitizenAllocationRepository extends CrudRepository<CitizenAllocationEntity, String> {
    @Modifying
    @Query("DELETE FROM CitizenAllocationEntity e WHERE e.gameId = :gameId")
    void deleteByGameId(@Param("gameId") String gameId);
}
