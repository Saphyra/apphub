package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.building_allocation;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface BuildingAllocationRepository extends CrudRepository<BuildingModuleAllocationEntity, String> {
    @Modifying
    @Query("DELETE FROM BuildingModuleAllocationEntity e WHERE e.gameId = :gameId")
    void deleteByGameId(@Param("gameId") String gameId);

    List<BuildingModuleAllocationEntity> getByGameId(String gameId, PageRequest page);
}
