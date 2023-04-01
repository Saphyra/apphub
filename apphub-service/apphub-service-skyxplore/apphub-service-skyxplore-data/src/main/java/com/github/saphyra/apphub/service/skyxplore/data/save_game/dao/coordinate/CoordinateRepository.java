package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.coordinate;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface CoordinateRepository extends JpaRepository<CoordinateEntity, String> {
    @Modifying
    @Query("DELETE FROM CoordinateEntity e WHERE e.gameId = :gameId")
    void deleteByGameId(@Param("gameId") String gameId);

    List<CoordinateEntity> getByReferenceId(String referenceId);

    List<CoordinateEntity> getByGameId(String gameId, PageRequest pageRequest);
}
