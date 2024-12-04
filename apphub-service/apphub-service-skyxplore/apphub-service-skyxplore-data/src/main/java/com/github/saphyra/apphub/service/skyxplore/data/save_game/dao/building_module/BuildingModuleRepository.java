package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.building_module;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

//TODO unit test
interface BuildingModuleRepository extends CrudRepository<BuildingModuleEntity, String> {
    @Modifying
    @Query("DELETE FROM BuildingModuleEntity e WHERE e.gameId = :gameId")
    void deleteByGameId(@Param("gameId") String gameId);

    List<BuildingModuleEntity> getByGameId(String gameId, PageRequest pageRequest);
}
