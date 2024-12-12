package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.construction_area;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface ConstructionAreaRepository extends CrudRepository<ConstructionAreaEntity, String> {
    @Modifying
    @Query("DELETE FROM ConstructionAreaEntity e WHERE e.gameId = :gameId")
    void deleteByGameId(@Param("gameId") String gameId);

    List<ConstructionAreaEntity> getByGameId(String gameId, PageRequest pageRequest);
}
