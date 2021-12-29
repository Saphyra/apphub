package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.construction;


import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface ConstructionRepository extends CrudRepository<ConstructionEntity, String> {
    @Modifying
    @Query("DELETE FROM ConstructionEntity e WHERE e.gameId = :gameId")
    void deleteByGameId(@Param("gameId") String gameId);

    List<ConstructionEntity> getByExternalReference(String location);
}
