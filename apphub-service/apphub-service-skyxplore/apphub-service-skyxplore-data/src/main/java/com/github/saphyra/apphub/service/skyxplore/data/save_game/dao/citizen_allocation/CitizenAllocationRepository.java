package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.citizen_allocation;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface CitizenAllocationRepository extends JpaRepository<CitizenAllocationEntity, String> {
    @Modifying
    @Query("DELETE FROM CitizenAllocationEntity e WHERE e.gameId = :gameId")
    void deleteByGameId(@Param("gameId") String gameId);

    List<CitizenAllocationEntity> getByGameId(String gameId, PageRequest pageRequest);
}
