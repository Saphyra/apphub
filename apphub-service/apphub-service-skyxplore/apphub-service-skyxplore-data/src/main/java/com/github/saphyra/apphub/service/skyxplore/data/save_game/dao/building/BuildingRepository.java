package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.building;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

interface BuildingRepository extends CrudRepository<BuildingEntity, String> {
    @Modifying
    @Query("DELETE FROM BuildingEntity e WHERE e.gameId = :gameId")
    void deleteByGameId(@Param("gameId") String gameId);

    Optional<BuildingEntity> findBySurfaceId(String surfaceId);

    List<BuildingEntity> getByGameId(String gameId, PageRequest pageRequest);
}
